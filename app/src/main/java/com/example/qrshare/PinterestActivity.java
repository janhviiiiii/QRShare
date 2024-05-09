package com.example.qrshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PinterestActivity extends AppCompatActivity {

    public final static int QRCodeWidth = 500;

    private EditText etxt_username;
    private Button btn_create;
    private ImageView img_gen_qr_code;
    private ImageButton btn_download;
    private ImageButton btn_share;
    private ImageButton btn_web_search;
    private ImageButton btn_favorite;
    private Bitmap bitmap;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    //for db
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinterest);

        // Set the initial title in onCreate or wherever needed
        getSupportActionBar().setTitle("Generate");


        etxt_username = findViewById(R.id.etxt_username);
        btn_create = findViewById(R.id.btn_create);
        img_gen_qr_code = findViewById(R.id.img_gen_qr_code);
        btn_download = findViewById(R.id.btn_download);
        btn_share = findViewById(R.id.btn_share);
        btn_web_search = findViewById(R.id.btn_web_search);
        btn_favorite = findViewById(R.id.btn_favorite);

        btn_download.setVisibility(View.INVISIBLE);
        btn_share.setVisibility(View.INVISIBLE);
        btn_web_search.setVisibility(View.INVISIBLE);
        btn_favorite.setVisibility(View.INVISIBLE);

        //for db
        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        //on btn_create click
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pinterestUsername = etxt_username.getText().toString();

                if(pinterestUsername.trim().isEmpty()) {
                    Toast.makeText(PinterestActivity.this, "Please fill all Mandatory fields", Toast.LENGTH_SHORT).show();
                }
                else{

                    String pinterestInfo = "Pinterest:\n" +
                            "https://www.pinterest.com/"+ pinterestUsername+"\n";

                    try{
                        bitmap = textToImageEncode(pinterestInfo);
                        img_gen_qr_code.setImageBitmap(bitmap);

                        btn_download.setVisibility(View.VISIBLE);
                        btn_share.setVisibility(View.VISIBLE);
                        btn_web_search.setVisibility(View.VISIBLE);
                        btn_favorite.setVisibility(View.VISIBLE);

                        //for db
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        byte[] imageByteArray = outputStream.toByteArray();

                        //for db
                        // Insert QR code details into database
                        String timestamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                        databaseHelper.insertQRCode(pinterestInfo, "GENERATED : Pinterest", timestamp, imageByteArray);

                        //download btn click
                        btn_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // Check if WRITE_EXTERNAL_STORAGE permission is granted
                                if (ContextCompat.checkSelfPermission(PinterestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Request the permission if it has not been granted
                                    ActivityCompat.requestPermissions(PinterestActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_WRITE_EXTERNAL_STORAGE);
                                } else {
                                    // Permission is already granted, proceed with saving the image
                                    saveImageToGallery(bitmap);
                                }
                            }
                        });


                        //method for btn_web_search
                        btn_web_search.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/"+pinterestUsername));
                                startActivity(i);
                            }
                        });


                        //fav btn click
                        // Share the scanned QR code result with others when the share button is clicked
                        btn_favorite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Save QR code to favorites
                                boolean isSuccess = databaseHelper.markAsFavorite(pinterestInfo, imageByteArray);
                                if (isSuccess) {
                                    // If successfully added to favorites, show a toast message
                                    Toast.makeText(PinterestActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If failed to add to favorites, show an error message
                                    Toast.makeText(PinterestActivity.this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
                                }
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
                                    Uri uri = getImageUri(PinterestActivity.this, bitmap);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission to the receiver app
                                    // Optionally, you can include a message in the sharing dialog
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing QR code image");
                                    // Start the sharing activity
                                    startActivity(Intent.createChooser(shareIntent, "Share QR Code Image"));
                                }
                                else {
                                    Toast.makeText(PinterestActivity.this, "Generate a QR code first", Toast.LENGTH_SHORT).show();
                                }
                            }

                            // Method to get the URI of the bitmap
                            private Uri getImageUri(PinterestActivity textActivity, Bitmap bitmap) {
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                String path = MediaStore.Images.Media.insertImage(textActivity.getContentResolver(), bitmap,
                                        "QR_Code", null);
                                return Uri.parse(path);
                            }
                        });

                    }
                    catch(WriterException e){
                        e.printStackTrace();
                        Toast.makeText(PinterestActivity.this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
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
                Toast.makeText(PinterestActivity.this, "Permission denied, unable to save image", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(PinterestActivity.this, "Saved to Device", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PinterestActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap textToImageEncode(String value) throws WriterException{
        BitMatrix bitMatrix;
        try{
            bitMatrix = new MultiFormatWriter().encode(value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QRCodeWidth, QRCodeWidth, null);

        }
        catch(IllegalArgumentException e){
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++){
            int offSet = y * bitMatrixWidth;
            for(int x = 0; x < bitMatrixWidth; x++){
                pixels[offSet + x] = bitMatrix.get(x, y)?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);

                // ContextCompat.getColor(this, R.color.black) : ContextCompat.getColor(this, R.color.white);

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
  //      bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);

        //for logo img purpose
        bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);


        // FOR LOGO DISPLAY
        // Overlay Pinterest logo image onto the QR code bitmap
        Canvas canvas = new Canvas(bitmap);
       // canvas.drawBitmap(bitmap, 0, 0, null); // Draw the QR code background

        // Load the logo image and resize it to fit within the QR code
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pinterest_1);
        // Define the desired width and height for the logo
        int desiredWidth = 70; // Adjust this value as needed
        int desiredHeight =70; // Adjust this value as needed
        // Scale down the logo bitmap to the desired size
//        Bitmap scaledLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, desiredWidth, desiredHeight, false);
        int left = (bitMatrixWidth - desiredWidth) / 2;
        int top = (bitMatrixHeight - desiredHeight) / 2;
        Rect rect = new Rect(left, top, left + desiredWidth, top + desiredHeight);
        canvas.drawBitmap(logoBitmap, null, rect, null);

        return bitmap;

    }
}