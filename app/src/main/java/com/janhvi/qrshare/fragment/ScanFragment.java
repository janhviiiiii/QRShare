package com.janhvi.qrshare.fragment;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.activity.QRCodeActivity;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScanFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = ScanFragment.class.getSimpleName();
    private View view;
    private ScrollView svScanFragment;
    private AppCompatButton btnCamera, btnGallery, btnScan;
    private ImageView ivQrCode;
    private TextView tvResult;
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

    // Flag to determine if the request is for camera or galleryw
    private boolean isCameraRequest = false;
    private boolean isGalleryRequest = false;

    private DbHelper dbHelper;

    public ScanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scan, container, false);

        initUI();
        initObj();
        initListener();

        return view;
    }

    private void initUI() {
        svScanFragment = view.findViewById(R.id.svScanFragment);
        btnCamera = view.findViewById(R.id.btnCamera);
        btnGallery = view.findViewById(R.id.btnGallery);
        ivQrCode = view.findViewById(R.id.ivQrCode);
        btnScan = view.findViewById(R.id.btnScan);
        tvResult = view.findViewById(R.id.tvResult);
    }

    private void initObj() {
        dbHelper = new DbHelper(getContext());
//init the array of permissions required to pick img from cam/gallery
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        //init BarcodeScanner with BarcodeScannerOptions
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);
    }

    private void initListener() {
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnScan.setOnClickListener(this);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (isCameraRequest) {
                        pickImageCamera();
                    } else {
                        pickImageGallery();
                    }
                } else {
                    Helper.makeSnackBar(svScanFragment, "Permission denied.");
                }
            });

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnCamera) {
            openCamera();
        } else if (id == R.id.btnGallery) {
            openGallery();
        } else if (id == R.id.btnScan) {
            onClickBtnScan();
        }
    }

    private void onClickBtnScan() {
        if (imageUri == null) {
            Helper.makeSnackBar(svScanFragment, "Pick Image first...");
        } else {
            detectResultFromImage();
        }
    }

    private void openCamera() {
        isCameraRequest = true;
        if (checkCameraPermissions()) {
            pickImageCamera();
        } else {
            requestCameraPermission();
        }
    }

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
                            Helper.makeSnackBar(svScanFragment, "Failed scanning due to " + e.getMessage().toString());
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "Failed due to " + e.getMessage().toString());
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

            //WiFi
            if (valueType == Barcode.TYPE_WIFI) {
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

                //url
            } else if (valueType == Barcode.TYPE_URL) {
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

                //email
            } else if (valueType == Barcode.TYPE_EMAIL) {
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

                //contact
            } else if (valueType == Barcode.TYPE_CONTACT_INFO) {
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

                //event
            } else if (valueType == Barcode.TYPE_CALENDAR_EVENT) {
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

                //sms
            } else if (valueType == Barcode.TYPE_SMS) {
                Barcode.Sms typeSms = barcode.getSms();

                String phoneNo = "" + typeSms.getPhoneNumber();
                String msg = "" + typeSms.getMessage();

                Log.d(TAG, "extractQRCodeInfo: TYPE_SMS");
                Log.d(TAG, "extractQRCodeInfo: phoneNo" + phoneNo);
                Log.d(TAG, "extractQRCodeInfo: message" + msg);


                resultBuilder.append("Type: TYPE_SMS \nphone number: ").append(phoneNo)
                        .append("\nmessage: ").append(msg)
                        .append("\nraw value").append(rawValues).append("\n");
            } else {
                resultBuilder.append(rawValues).append("\n");
            }

        }
        BitmapDrawable drawable = (BitmapDrawable) ivQrCode.getDrawable();

        Bitmap bitmap = drawable.getBitmap();
        ivQrCode.setImageBitmap(bitmap);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageByteArray = outputStream.toByteArray();

        String content = resultBuilder.toString();
        QRCode qrCode = new QRCode();
        qrCode.setContent(content);
        qrCode.setType("Scanned");
        qrCode.setDate(Helper.getCurrentDate());
        qrCode.setTime(Helper.getCurrentTime());
        qrCode.setImage(imageByteArray);
        qrCode.setIsFavorite(0);
        //adding qrcode hto db for history
        dbHelper.addOrUpdateQRCode(qrCode);
        // Set the final result to the txt_result TextView
        tvResult.setText(resultBuilder.toString());
        //going to next screen
        Helper.goToAndFinish(getContext(), QRCodeActivity.class, Constants.QRCODE, qrCode);
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private void pickImageCamera() {
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
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        //img picked, get the uri of the img picked
                        Intent data = result.getData();

                        if (data != null) {
                            ivQrCode.setVisibility(View.VISIBLE);
                            imageUri = data.getData();
                            ivQrCode.setImageURI(imageUri);
                        }
                    } else {
                        Helper.makeSnackBar(svScanFragment, "Cancelled");

                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        ivQrCode.setVisibility(View.VISIBLE);
                        ivQrCode.setImageURI(imageUri);
                    } else {
                        Helper.makeSnackBar(svScanFragment, "Cancelled...");
                    }
                }
            }
    );

    public boolean checkStoragePermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission() {
        // ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public boolean checkCameraPermissions() {
        boolean resultCamera = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        boolean resultStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        return resultCamera && resultStorage;
    }
    public void requestCameraPermission() {
        //ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            // Set the title of the action bar
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("SCAN");
        }
    }
}
