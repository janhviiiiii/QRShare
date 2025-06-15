package com.janhvi.qrshare.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;
public class WifiActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = WifiActivity.class.getSimpleName();
    private RelativeLayout rlWifiActivity;
    private TextInputEditText etNetworkName, etPassword;
    private RadioGroup radioGroup;
    private AppCompatRadioButton rbWpaWpa2, rbWep, rbNoEncryption;
    private AppCompatButton btnSubmit;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        initToolbar();
        initUI();
        initObj();
        initListener();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WiFi");
    }

    private void initUI() {
        rlWifiActivity = findViewById(R.id.rlWifiActivity);
        etNetworkName = findViewById(R.id.etNetworkName);
        etPassword = findViewById(R.id.etPassword);
        radioGroup = findViewById(R.id.radioGroup);
        rbWpaWpa2 = findViewById(R.id.rbWpaWpa2);
        rbWep = findViewById(R.id.rbWep);
        rbNoEncryption = findViewById(R.id.rbNoEncryption);
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
        // Validate required fields
        View[] view = {etNetworkName};
        if (Helper.isEmptyFieldValidation(view)) {

            String networkName = Helper.getStringFromInput(etNetworkName);
            String password = Helper.getStringFromInput(etPassword);

            String encryptionType = "";
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == rbWpaWpa2.getId()) {
                encryptionType = "WPA";
            } else if (selectedId == rbWep.getId()) {
                encryptionType = "WEP";
            } else if (selectedId == rbNoEncryption.getId()) {
                encryptionType = "nopass";
                password = "";  // clear password if no encryption selected
            }

            // Additional validation: if encryption requires password, check password field
            if (!encryptionType.equals("nopass") && password.isEmpty()) {
                Helper.makeSnackBar(rlWifiActivity, "Please enter password for secured network");
                return;
            }

            // Create WiFi QR code content
            String content = "WIFI:S:" + networkName + ";T:" + encryptionType + ";P:" + password + ";;";

            Bitmap bitmap = Helper.textToImageEncode(context, content);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                QRCode qrCode = new QRCode();
                qrCode.setContent(content);
                qrCode.setType("WiFi");
                qrCode.setDate(Helper.getCurrentDate());
                qrCode.setTime(Helper.getCurrentTime());
                qrCode.setImage(imageBytes);
                qrCode.setIsFavorite(0);

                long result = dbHelper.addOrUpdateQRCode(qrCode);
                qrCode.setQid(result);
                Log.d(TAG, "qid: " + qrCode.getQid());
                if (result != -1) {
                    Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                } else {
                    Helper.makeSnackBar(rlWifiActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlWifiActivity, "Error generating QR code");
            }
        }
    }
}
