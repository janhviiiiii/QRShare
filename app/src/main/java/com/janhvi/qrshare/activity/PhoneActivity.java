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
import com.hbb20.CountryCodePicker;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = PhoneActivity.class.getSimpleName();
    private RelativeLayout rlPhoneActivity;
    private CountryCodePicker countryCodePicker;
    private TextInputEditText etPhoneNo;
    private AppCompatButton btnSubmit;
    private String countryCode;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        initToolbar();
        initUI();
        initObj();
        initListener();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Phone");
    }

    private void initUI() {
        rlPhoneActivity = findViewById(R.id.rlPhoneActivity);
        etPhoneNo = findViewById(R.id.etPhoneNo);
        countryCodePicker = findViewById(R.id.countryCodePicker);
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
        View[] view = {etPhoneNo};
        if (Helper.isEmptyFieldValidation(view)) {
            // Always get latest selected country code
            countryCode = countryCodePicker.getSelectedCountryCode();

            if (countryCode == null || countryCode.isEmpty()) {
                Helper.makeSnackBar(rlPhoneActivity, "Please, select country code");
                return;
            }

            String phoneNumber = Helper.getStringFromInput(etPhoneNo);
            String content = "Phone: +" + countryCode + phoneNumber;
            Bitmap bitmap = Helper.textToImageEncode(context, content);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap); // convert Bitmap to byte[]
                QRCode qrCode = new QRCode();
                qrCode.setContent(content);
                qrCode.setType("Phone");
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
                    Helper.makeSnackBar(rlPhoneActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlPhoneActivity, "Error generating QR code");
            }
        }
    }

}