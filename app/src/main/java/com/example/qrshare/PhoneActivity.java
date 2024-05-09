package com.example.qrshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class PhoneActivity extends AppCompatActivity {

    public final static int QRCodeWidth = 500;

    private EditText etxt_phone_no;
    private CountryCodePicker countryCodePicker;
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
        setContentView(R.layout.activity_phone);

        // Set the initial title in onCreate or wherever needed
        getSupportActionBar().setTitle("Generate");


        etxt_phone_no = findViewById(R.id.etxt_phone_no);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        btn_create = findViewById(R.id.btn_create);
        img_gen_qr_code = findViewById(R.id.img_gen_qr_code);
        btn_download = findViewById(R.id.btn_download);
        btn_share = findViewById(R.id.btn_share);
        btn_web_search = findViewById(R.id.btn_web_search);
        btn_favorite = findViewById(R.id.btn_favorite);

        //btn visibility
        btn_download.setVisibility(View.INVISIBLE);
        btn_share.setVisibility(View.INVISIBLE);
        btn_web_search.setVisibility(View.INVISIBLE);
        btn_favorite.setVisibility(View.INVISIBLE);

        //for db
        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        findViewById(R.id.btn_create).setOnClickListener(view -> validatePhoneNumber());

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
                Toast.makeText(PhoneActivity.this, "Permission denied, unable to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // Update the method to include current timestamp
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
            Toast.makeText(PhoneActivity.this, "Saved to Device", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PhoneActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
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
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void validatePhoneNumber() {
        String phoneNumber = etxt_phone_no.getText().toString().trim();

        // Check if the phone number is valid
        if (isValidPhoneNumber(phoneNumber)) {

            //btn_create click
            btn_create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String phoneNumber = etxt_phone_no.getText().toString();
                    String countryCode = countryCodePicker.getDefaultCountryCode().toString();

                    if(phoneNumber.trim().isEmpty()){
                        Toast.makeText(PhoneActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        String phoneInfo = "BEGIN:PHONE\n" +
                                "PHONE NO:" +countryCode+ phoneNumber + "\n" +
                                "END:PHONE"+"\n";
                        try{
                            bitmap = textToImageEncode(phoneInfo);
                            img_gen_qr_code.setImageBitmap(bitmap);

                            //btn visibility
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
                            databaseHelper.insertQRCode(phoneInfo, "GENERATED : Phone", timestamp, imageByteArray);

                            //download btn click
                            btn_download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    // Check if WRITE_EXTERNAL_STORAGE permission is granted
                                    if (ContextCompat.checkSelfPermission(PhoneActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        // Request the permission if it has not been granted
                                        ActivityCompat.requestPermissions(PhoneActivity.this,
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
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q="+countryCode+phoneNumber));
                                    startActivity(i);
                                }
                            });


                            //fav btn click
                            // Share the scanned QR code result with others when the share button is clicked
                            btn_favorite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Save QR code to favorites
                                    boolean isSuccess = databaseHelper.markAsFavorite(phoneInfo, imageByteArray);
                                    if (isSuccess) {
                                        // If successfully added to favorites, show a toast message
                                        Toast.makeText(PhoneActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If failed to add to favorites, show an error message
                                        Toast.makeText(PhoneActivity.this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
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
                                        Uri uri = getImageUri(PhoneActivity.this, bitmap);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission to the receiver app
                                        // Optionally, you can include a message in the sharing dialog
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing QR code image");
                                        // Start the sharing activity
                                        startActivity(Intent.createChooser(shareIntent, "Share QR Code Image"));
                                    }
                                    else {
                                        Toast.makeText(PhoneActivity.this, "Generate a QR code first", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                // Method to get the URI of the bitmap
                                private Uri getImageUri(PhoneActivity textActivity, Bitmap bitmap) {
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
                            Toast.makeText(PhoneActivity.this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();

                        }


                    }
                }
            });

        }
        else {
            // Display a toast or set an error message in the EditText
            etxt_phone_no.setError("Enter a valid phone number");
            // Or display a Toast message
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // You can customize this validation based on your requirements.
        // Here, we are using Android's built-in Patterns class to check for a valid phone number.
        return !TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches();
    }
}