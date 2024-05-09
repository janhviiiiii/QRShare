package com.example.qrshare;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRCodeDetailActivity extends AppCompatActivity {

    private ImageView img_gen_qr_code;
    private TextView textContent;
    private TextView textType;
    private TextView textTimestamp;
    private ImageButton btn_download;
    private ImageButton btn_share;
    private ImageButton btn_web_search;
    private ImageButton btn_copy;
    private Bitmap bitmap;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    //for db
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_detail);

        // Set the initial title in onCreate or wherever needed
        getSupportActionBar().setTitle("QR Code Details");


        img_gen_qr_code = findViewById(R.id.img_gen_qr_code);
        textContent = findViewById(R.id.textContent);
        textType = findViewById(R.id.textType);
        textTimestamp = findViewById(R.id.textTimestamp);
        btn_download = findViewById(R.id.btn_download);
        btn_share = findViewById(R.id.btn_share);
        btn_web_search = findViewById(R.id.btn_web_search);
        btn_copy = findViewById(R.id.btn_copy);

        //for db
        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);


        //receiving details from intent
        Intent intent = getIntent();
        if (intent != null) {
            byte[] imageByteArray = intent.getByteArrayExtra("image");
            String content = intent.getStringExtra("content");
            String type = intent.getStringExtra("type");
            String timestamp = intent.getStringExtra("timestamp");


            if (imageByteArray != null) {
                 bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                img_gen_qr_code.setImageBitmap(bitmap);
            }

            textContent.setText("Content: " + content);
            textType.setText("Type: " + type);
            textTimestamp.setText("Timestamp: " + timestamp);


            //btn logic

            //download btn click
            btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check if WRITE_EXTERNAL_STORAGE permission is granted
                    if (ContextCompat.checkSelfPermission(QRCodeDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request the permission if it has not been granted
                        ActivityCompat.requestPermissions(QRCodeDetailActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        // Permission is already granted, proceed with saving the image
                        saveImageToGallery(bitmap);
                    }
                }
            });


            //btn_web_search
            btn_web_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + content));
                    startActivity(i);
                }
            });


            // Share the scanned QR code result with others when the share button is clicked
            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (bitmap != null) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/jpeg"); // Set the MIME type to image/jpeg for sharing images
                        // Convert the bitmap to a URI and add it to the intent
                        Uri uri = getImageUri(QRCodeDetailActivity.this, bitmap);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission to the receiver app
                        // Optionally, you can include a message in the sharing dialog
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing QR code image");
                        // Start the sharing activity
                        startActivity(Intent.createChooser(shareIntent, "Share QR Code Image"));
                    }
                    else {
                        Toast.makeText(QRCodeDetailActivity.this, "Image not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            // Copy the scanned QR code result to the clipboard when the copy button is clicked
            btn_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("QR Code Result", content);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(QRCodeDetailActivity.this, "Content copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    // Method to get the URI of the bitmap
    private Uri getImageUri(QRCodeDetailActivity qrCodeDetailActivity, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(qrCodeDetailActivity.getContentResolver(), bitmap,
                "QR_Code", null);
        return Uri.parse(path);
    }


    // Override onRequestPermissionsResult to handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with saving the image
                saveImageToGallery(bitmap);
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(QRCodeDetailActivity.this, "Permission denied, unable to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveImageToGallery(Bitmap bitmap) {
        // Create a directory to save the image
        File imagesFolder = new File(getExternalFilesDir(null), "Images");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }

        String timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "QR_Code_" + timestamp+ ".jpg";

        // Save the bitmap to a file
        File file = new File(imagesFolder, fileName);
        try {
            OutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            // Update the media store so the image appears in the gallery app
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
            Toast.makeText(QRCodeDetailActivity.this, "Saved to Device", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(QRCodeDetailActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }

    }
}