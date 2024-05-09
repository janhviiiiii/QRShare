package com.example.qrshare;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Scan_Result extends AppCompatActivity {


    //variable declare
    private ImageView img_qr_code;
    private TextView txt_result;
    private ImageButton btn_copy, btn_share, btn_web_search, btn_favorite;
    private Bitmap bitmap;

    //for db
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);


        // Set the initial title in onCreate or wherever needed
        getSupportActionBar().setTitle("Scan");


        //for db
        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);


        img_qr_code = findViewById(R.id.img_qr_code);
        txt_result = findViewById(R.id.txt_result);
        btn_copy = findViewById(R.id.btn_copy);
        btn_share = findViewById(R.id.btn_share);
        btn_web_search = findViewById(R.id.btn_web_search);
        btn_favorite = findViewById(R.id.btn_favorite);


        // Get the scanned QR code result from the intent
        String scannedResult = getIntent().getStringExtra("scanned_result");
        txt_result.setText(scannedResult);

        // Get the QR code image URI from the intent and display it in the ImageView
        Uri qrCodeImageUri = getIntent().getParcelableExtra("qr_code_image_uri");
        if (qrCodeImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), qrCodeImageUri);
                img_qr_code.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //fav btn click
        // Share the scanned QR code result with others when the share button is clicked
        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //converting the imf in img_qr_code to byte array
                BitmapDrawable drawable = (BitmapDrawable) img_qr_code.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] imageByteArray = outputStream.toByteArray();
                // String timestamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());


                // Save QR code to favorites
                boolean isSuccess = databaseHelper.markAsFavorite(scannedResult, imageByteArray);
                if (isSuccess) {
                    // If successfully added to favorites, show a toast message
                    Toast.makeText(Scan_Result.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
                else {
                    // If failed to add to favorites, show an error message
                    Toast.makeText(Scan_Result.this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Copy the scanned QR code result to the clipboard when the copy button is clicked
        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("QR Code Result", scannedResult);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(Scan_Result.this, "Result copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


        //method for btn_web_search
        btn_web_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q="+scannedResult));
                startActivity(i);
            }
        });


        // Share the scanned QR code result with others when the share button is clicked
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, scannedResult);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code Result"));
            }
        });
    }
}