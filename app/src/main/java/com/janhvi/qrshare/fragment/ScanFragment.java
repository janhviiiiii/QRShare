package com.janhvi.qrshare.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.io.IOException;
import java.util.List;
public class ScanFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ScanFragment.class.getSimpleName();
    private View view;
    private ScrollView svScanFragment;
    private AppCompatButton btnCamera, btnGallery, btnScan;
    private ImageView ivQrCode;
    private TextView tvResult;
    private byte[] imageByteArray;
    private Uri imageUri = null;
    private boolean isCameraRequest = false;

    private BarcodeScanner barcodeScanner;
    private DbHelper dbHelper;
    private static final int CAMERA_REQUEST_CODE = 100;

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
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
    }

    private void initListener() {
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCamera) {
            isCameraRequest = true;
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                pickImageCamera();
            } else {
                requestCameraPermission();
            }
        } else if (v.getId() == R.id.btnGallery) {
            isCameraRequest = false;
            pickImageGallery();
        } else if (v.getId() == R.id.btnScan) {
            if (imageUri == null) {
                Helper.makeSnackBar(svScanFragment, "Pick Image first...");
            } else {
                detectResultFromImage();
            }
        }
    }

    private void pickImageCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }

    private void detectResultFromImage() {
        try {
            InputImage inputImage = InputImage.fromFilePath(requireContext(), imageUri);
            barcodeScanner.process(inputImage)
                    .addOnSuccessListener(this::extractBarCodeQRCodeInfo)
                    .addOnFailureListener(e -> {
                        Helper.makeSnackBar(svScanFragment, "Failed scanning: " + e.getMessage());
                        Log.e(TAG, "Error: ", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "detectResultFromImage Exception: ", e);
        }
    }

    private void extractBarCodeQRCodeInfo(List<Barcode> barcodes) {
        StringBuilder resultBuilder = new StringBuilder();

        for (Barcode barcode : barcodes) {
            String rawValue = barcode.getRawValue();
            int valueType = barcode.getValueType();

            switch (valueType) {
                case Barcode.TYPE_WIFI:
                    Barcode.WiFi wifi = barcode.getWifi();
                    if (wifi != null) {
                        resultBuilder.append("Type: WIFI\nSSID: ").append(wifi.getSsid())
                                .append("\nPassword: ").append(wifi.getPassword()).append("\n");
                    }
                    break;

                case Barcode.TYPE_URL:
                    Barcode.UrlBookmark urlBookmark = barcode.getUrl();
                    if (urlBookmark != null) {
                        resultBuilder.append("Type: URL\nTitle: ").append(urlBookmark.getTitle())
                                .append("\nURL: ").append(urlBookmark.getUrl()).append("\n");
                    }
                    break;

                case Barcode.TYPE_EMAIL:
                    Barcode.Email email = barcode.getEmail();
                    if (email != null) {
                        resultBuilder.append("Type: EMAIL\nAddress: ").append(email.getAddress())
                                .append("\nSubject: ").append(email.getSubject())
                                .append("\nBody: ").append(email.getBody()).append("\n");
                    }
                    break;

                case Barcode.TYPE_CONTACT_INFO:
                    Barcode.ContactInfo contactInfo = barcode.getContactInfo();
                    if (contactInfo != null) {
                        resultBuilder.append("Type: CONTACT\nName: ")
                                .append(contactInfo.getName() != null ? contactInfo.getName().getFormattedName() : "")
                                .append("\nPhones: ").append(contactInfo.getPhones().size() > 0 ? contactInfo.getPhones().get(0).getNumber() : "")
                                .append("\nEmails: ").append(contactInfo.getEmails().size() > 0 ? contactInfo.getEmails().get(0).getAddress() : "").append("\n");
                    }
                    break;

                case Barcode.TYPE_SMS:
                    Barcode.Sms sms = barcode.getSms();
                    if (sms != null) {
                        resultBuilder.append("Type: SMS\nPhone: ").append(sms.getPhoneNumber())
                                .append("\nMessage: ").append(sms.getMessage()).append("\n");
                    }
                    break;

                default:
                    resultBuilder.append("Raw Value: ").append(rawValue).append("\n");
            }
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByteArray = stream.toByteArray();

            QRCode qrCode = new QRCode();
            qrCode.setContent(resultBuilder.toString());
            qrCode.setType(Constants.SCANNED);
            qrCode.setDate(Helper.getCurrentDate());
            qrCode.setTime(Helper.getCurrentTime());
            qrCode.setImage(imageByteArray);
            qrCode.setIsFavorite(0);
            dbHelper.addOrUpdateQRCode(qrCode);

            tvResult.setText(resultBuilder.toString());
            Helper.goToAndFinish(getContext(), QRCodeActivity.class, Constants.QRCODE, qrCode);
        } catch (IOException e) {
            Log.e(TAG, "Bitmap conversion failed: ", e);
        }
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivQrCode.setVisibility(View.VISIBLE);
                    btnScan.setVisibility(View.VISIBLE);
                    ivQrCode.setImageURI(imageUri);
                } else {
                    Helper.makeSnackBar(svScanFragment, "Cancelled");
                }
            });

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ivQrCode.setVisibility(View.VISIBLE);
                    btnScan.setVisibility(View.VISIBLE);
                    ivQrCode.setImageURI(imageUri);
                } else {
                    Helper.makeSnackBar(svScanFragment, "Cancelled...");
                }
            });

    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (isCameraRequest) {
                        pickImageCamera();
                    }
                } else {
                    Helper.makeSnackBar(svScanFragment, "Permission Denied");
                }
            });

}
