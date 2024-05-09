package com.example.qrshare;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;



public class Scan_Fragment extends Fragment {

    //UI Views
    private MaterialButton btn_camera;
    private MaterialButton btn_gallery;
    private ImageView img_android;
    private TextView txt_result;
    private MaterialButton btn_scan;

    private Bitmap bitmap;
    private byte[] imageByteArray; //to store the img in db


    //to handle the result of cam/gallery permissions in onRequestPermissionResults
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    //arrays of permissions requires to pick image from cam/gallery
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //Uri of the img that we will take from cam/gallery
    private Uri imageUri = null;

    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;


    private static final String TAG = "MAIN_TAG";


    // Flag to determine if the request is for camera or galleryw
    private boolean isCameraRequest = false;
    private boolean isGalleryRequest = false;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (isCameraRequest) {
                        pickImageCamera();
                    } else {
                        pickImageGallery();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_, container, false);

        // Your other fragment initialization code here
        //init UI Views
        btn_camera = view.findViewById(R.id.btn_camera);
        btn_gallery = view.findViewById(R.id.btn_gallery);
        img_android = view.findViewById(R.id.img_android);
        btn_scan = view.findViewById(R.id.btn_scan);
        txt_result = view.findViewById(R.id.txt_result);


        //init the array of permissions required to pick img from cam/gallery
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        //init BarcodeScanner with BarcodeScannerOptions
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);

        //camera btn click
        btn_camera.setOnClickListener(v -> openCamera());

        //gallery btn click
        btn_gallery.setOnClickListener(v -> openGallery());

        //handles scan btn click
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null) {
                    //image is not selected
                    Toast.makeText(getContext(), "Pick Image first...", Toast.LENGTH_SHORT).show();
                }
                else {
                    //img was picked, start scanning qr code
                    detectResultFromImage();
                }
            }
        });

        return view;
    }


    // Method to handle camera button click
    private void openCamera() {
        isCameraRequest = true;
        if (checkCameraPermissions()) {
            pickImageCamera();
        }
        else {
            requestCameraPermission();
        }
    }

    // Method to handle gallery button click
    private void openGallery() {
        isCameraRequest = false;
        if (checkStoragePermissions()) {
            pickImageGallery();
        } else {
            requestStoragePermission();
        }
    }


    private void detectResultFromImage() {
        try {

            //prepare img from img_url
            InputImage inputImage = InputImage.fromFilePath(getContext(), imageUri);

            //start scanning the qr code data from image
            Task<List<Barcode>> barcodeResult = barcodeScanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            //task completed successfully, we can get detailed info now
                            extractBarCodeQRCodeInfo(barcodes);

                            //for db fav btn
                            //set bitmap here
//                            imageByteArray = bitmapToByteArray(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed scanning due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e) {
            Toast.makeText(getContext(), "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //extracting info from the qrcode and storing the result
    private void extractBarCodeQRCodeInfo(List<Barcode> barcodes) {

        //new StringBuilder
        StringBuilder resultBuilder = new StringBuilder();

        //get info from barcodes
        for (Barcode barcode : barcodes) {

            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            //raw info scanned from QR Code
            String rawValues = barcode.getRawValue();
            Log.d(TAG, "extractBarCodeQRCodeInfo: rawValue: " + rawValues);

            int valueType = barcode.getValueType();

            switch (valueType) {

                //WiFi
                case Barcode.TYPE_WIFI: {
                    Barcode.WiFi typeWifi = barcode.getWifi();

                    String ssid = "" + typeWifi.getSsid();
                    String password = "" + typeWifi.getPassword();
                    String encryptionType = "" + typeWifi.getEncryptionType();

                    Log.d(TAG, "extractBarCodeQRCodeInfo: ssid: " + ssid);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: password: " + password);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: encryptionType: " + encryptionType);

//                    txt_result.setText("Type: TYPE_WIFI \nssid: " + ssid + "\npassword: " + password + "\nencryptionType: " + encryptionType + "\nraw value" + rawValues);
                    resultBuilder.append("Type: TYPE_WIFI \nssid: ").append(ssid)
                            .append("\npassword: ").append(password)
                            .append("\nencryptionType: ").append(encryptionType)
                            .append("\n").append(rawValues).append("\n");
                }
                break;

                //url
                case Barcode.TYPE_URL: {
                    Barcode.UrlBookmark typeUrl = barcode.getUrl();

//                    assert typeUrl != null;
                    String title = "" + typeUrl.getTitle();
                    String url = "" + typeUrl.getUrl();

                    Log.d(TAG, "extractQRCodeInfo: TYPE_URL");
                    Log.d(TAG, "extractQRCodeInfo: title" + title);
                    Log.d(TAG, "extractQRCodeInfo: url" + url);

//                    txt_result.setText("Type: TYPE_URL \ntitle: " + title + "\nurl: " + url + "\nraw value" + rawValues);
                    resultBuilder.append("Type: TYPE_URL \ntitle: ").append(title)
                            .append("\nurl: ").append(url)
                            .append("\nraw value").append(rawValues).append("\n");

                }
                break;

                //email
                case Barcode.TYPE_EMAIL: {
                    Barcode.Email typeEmail = barcode.getEmail();

//                    assert typeEmail != null;
                    String address = "" + typeEmail.getAddress();
                    String subject = "" + typeEmail.getSubject();
                    String body = "" + typeEmail.getBody();

                    Log.d(TAG, "extractQRCodeInfo: TYPE_EMAIL");
                    Log.d(TAG, "extractQRCodeInfo: address" + address);
                    Log.d(TAG, "extractQRCodeInfo: subject" + subject);
                    Log.d(TAG, "extractQRCodeInfo: content" + body);

//                    txt_result.setText("Type: TYPE_EMAIL \naddress: " + address + "\nsubject: " + subject + "\nbody: " + body + "\nraw value" + rawValues);
                    resultBuilder.append("Type: TYPE_EMAIL \naddress: ").append(address)
                            .append("\nsubject: ").append(subject)
                            .append("\nbody: ").append(body)
                            .append("\nraw value").append(rawValues).append("\n");

                }
                break;

                //contact
                case Barcode.TYPE_CONTACT_INFO: {
                    Barcode.ContactInfo typeContact = barcode.getContactInfo();

                    String title = "" + typeContact.getTitle();
                    String name = "" + typeContact.getName().getFirst() + " " + typeContact.getName().getLast();
                    String phone = "" + typeContact.getPhones().get(0).getNumber();
                    String email = "" + typeContact.getEmails();

                    Log.d(TAG, "extractQRCodeInfo: TYPE_CONTACT");
                    Log.d(TAG, "extractQRCodeInfo: title" + title);
                    Log.d(TAG, "extractQRCodeInfo: name" + name);
                    Log.d(TAG, "extractQRCodeInfo: phone" + phone);
                    Log.d(TAG, "extractQRCodeInfo: email" + email);

//                    txt_result.setText("Type: TYPE_CONTACT \ntitle: " + title + "\nname: " + name + "\nphone no: " + phone + "\nemail: " + email + "\nraw value" + rawValues);
                    resultBuilder.append("Type: TYPE_CONTACT \ntitle: ").append(title)
                            .append("\nname: ").append(name)
                            .append("\nphone no: ").append(phone)
                            .append("\nemail: ").append(email)
                            .append("\nraw value").append(rawValues).append("\n");
                }
                break;

                //event
                case Barcode.TYPE_CALENDAR_EVENT:{
                    Barcode.CalendarEvent typeCalendar = barcode.getCalendarEvent();

                    String title = "" + typeCalendar.getOrganizer();
                    String start = "" + typeCalendar.getStart();
                    String end = "" + typeCalendar.getEnd();
                    String summary = "" + typeCalendar.getSummary();
                    String location = "" + typeCalendar.getLocation();
                    String description = "" + typeCalendar.getDescription();

                    Log.d(TAG, "extractQRCodeInfo: TYPE_CALENDAR_EVENT");
                    Log.d(TAG, "extractQRCodeInfo: organizer" + title);
                    Log.d(TAG, "extractQRCodeInfo: start" + start);
                    Log.d(TAG, "extractQRCodeInfo: end" + end);
                    Log.d(TAG, "extractQRCodeInfo: summary" + summary);
                    Log.d(TAG, "extractQRCodeInfo: location" + location);
                    Log.d(TAG, "extractQRCodeInfo: description" + description);

//                    txt_result.setText("TYPE: TYPE_CALENDAR_EVENT \norganizer: "+title+"\nstart: "+start+"\nend: "+end+"\nlocation: "+location+"\ndescription: "+description);
                    resultBuilder.append("Type: TYPE_CALENDAR_EVENT \norganizer: ").append(title)
                            .append("\nstart: ").append(start)
                            .append("\nend: ").append(end)
                            .append("\nlocation: ").append(location)
                            .append("\nsummary: ").append(summary)
                            .append("\ndescription").append(description)
                            .append("\nraw value").append(rawValues).append("\n");
                }
                break;

                //sms
                case Barcode.TYPE_SMS:{
                    Barcode.Sms typeSms = barcode.getSms();

                    String phoneNo = "" + typeSms.getPhoneNumber();
                    String msg = "" + typeSms.getMessage();

                    Log.d(TAG, "extractQRCodeInfo: TYPE_SMS");
                    Log.d(TAG, "extractQRCodeInfo: phoneNo" + phoneNo);
                    Log.d(TAG, "extractQRCodeInfo: message" + msg);


                    resultBuilder.append("Type: TYPE_SMS \nphone number: ").append(phoneNo)
                            .append("\nmessage: ").append(msg)
                            .append("\nraw value").append(rawValues).append("\n");
                }
                break;

                //default
                default: {
//                    txt_result.setText("raw value: " + rawValues);
                    resultBuilder.append(rawValues).append("\n");
                }

            }

        }

        ///Scan_Result page
        // Redirecting to Scan_Result activity to display the scanned result

        // Set the final result to the txt_result TextView
        txt_result.setText(resultBuilder.toString());

        Intent intent = new Intent(getContext(), Scan_Result.class);
        intent.putExtra("scanned_result", resultBuilder.toString()); //storing the result of scanned qr code in scanned_result
        intent.putExtra("qr_code_image_uri", imageUri); //storing the image of qr code to qr_code_image_uri
        startActivity(intent);


        //for db
        //set bitmap here
        BitmapDrawable drawable = (BitmapDrawable) img_android.getDrawable();

        Bitmap bitmap = drawable.getBitmap();
        img_android.setImageBitmap(bitmap);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageByteArray = outputStream.toByteArray();

        // Inside extractBarCodeQRCodeInfo method after displaying result
        // Insert scanned QR code details into the database
        String timestamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.insertQRCode(resultBuilder.toString(), "SCANNED", timestamp, imageByteArray);

    }


    private void pickImageGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }


    private void  pickImageCamera(){

        //img data store to MediaStore
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");

        //imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);

    }


    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {

                    //img picked will receive the img, if img picked from gallery
                    if(result.getResultCode() == Activity.RESULT_OK) {

                        //img picked, get the uri of the img picked
                        Intent data = result.getData();

                        if (data != null) {
                            imageUri = data.getData();
                            img_android.setImageURI(imageUri);
                        }
                    }
                    else {
                        //cancelled
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );


    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {

                    //here we will receive the img, if taken from camera
                    if(result.getResultCode() == Activity.RESULT_OK){

                        //img taken from cam
                        // Intent data = result.getData();

                        // Log.d(TAG, "onActivityResult: imageUri: "+imageUri);
                        //set the img view
                        img_android.setImageURI(imageUri);

                    }
                    else {
                        //cancelled
                        Toast.makeText(getContext(), "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }

            }
    );


    public boolean checkStoragePermissions() {

        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    public void requestStoragePermission(){

        // ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }


    public boolean checkCameraPermissions(){

        boolean resultCamera = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        boolean resultStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        return resultCamera && resultStorage;
    }


    public void requestCameraPermission(){

        //ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);

    }

//    //hide action bar
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Show the action bar
//        if (getActivity() != null) {
//            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
//        }
//    }

    //for custom Action bar name
    @Override
    public void onResume() {

        super.onResume();
        if (getActivity() != null) {
            // Set the title of the action bar
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("SCAN");

        }
    }

}