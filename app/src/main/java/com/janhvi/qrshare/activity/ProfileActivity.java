package com.janhvi.qrshare.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ProfileActivity.class.getSimpleName();
    private RelativeLayout rlProfileActivity;
    private TextInputEditText etName, etEmail, etAddress, etContact, etCompanyName, etJobTitle;
    private AppCompatButton btnSubmit;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        initUI();
        initObj();
        initListener();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
    }

    private void initUI() {
        rlProfileActivity = findViewById(R.id.rlProfileActivity);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);
        etAddress = findViewById(R.id.etAddress);
        etCompanyName = findViewById(R.id.etCompanyName);
        etJobTitle = findViewById(R.id.etJobTitle);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
        entity = new QRCode();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit) {
            try {
                onClickBtnSubmit();
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onClickBtnSubmit() throws WriterException {
        View[] view = {etName, etEmail, etContact, etAddress};
        if (Helper.isEmptyFieldValidation(view) && Helper.isContactValid(etContact) && Helper.isEmailValid(etEmail)) {
            String content = "Company name: " + Helper.getStringFromInput(etName) +
                    "\nEmail: " + Helper.getStringFromInput(etEmail) +
                    "\nContact: " + Helper.getStringFromInput(etContact) +
                    "\nAddress: " + Helper.getStringFromInput(etAddress) +
                    "\nJob Title: " + ((Objects.equals(Helper.getStringFromInput(etJobTitle), "") || Objects.requireNonNull(Helper.getStringFromInput(etJobTitle)).isEmpty()) ? "N/A" : Helper.getStringFromInput(etJobTitle)) +
                    "\nCompany Name: " + ((Objects.equals(Helper.getStringFromInput(etCompanyName), "") || Objects.requireNonNull(Helper.getStringFromInput(etJobTitle)).isEmpty()) ? "N/A" : Helper.getStringFromInput(etCompanyName));
//            String content = Helper.getStringFromInput(etText);
            Bitmap bitmap = Helper.textToImageEncode(context, content);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap); // convert Bitmap to byte[]
                QRCode qrCode = new QRCode();
                qrCode.setContent(content);
                qrCode.setType("Profile");
                qrCode.setDate(Helper.getCurrentDate());
                qrCode.setTime(Helper.getCurrentTime());
                qrCode.setImage(imageBytes);
                qrCode.setIsFavorite(0);

                long result = dbHelper.addOrUpdateQRCode(qrCode);
                qrCode.setQid(result);
                Log.d(TAG, "qid: " + qrCode.getQid());
                if (result != -1) {
                    // Helper.makeSnackBar(rlTextActivity, "QR Code saved");
                    Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                } else {
                    Helper.makeSnackBar(rlProfileActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlProfileActivity, "Error generating QR code");
            }
        }
    }
}