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

public class LocationActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = LocationActivity.class.getSimpleName();
    private RelativeLayout rlLocationActivity;
    private TextInputEditText etLatitude, etLongitude;
    private AppCompatButton btnSubmit;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initToolbar();
        initUI();
        initObj();
        initListener();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Location");
    }

    private void initUI() {
        rlLocationActivity = findViewById(R.id.rlLocationActivity);
        etLatitude = findViewById(R.id.etName);
        etLongitude = findViewById(R.id.etSubject);
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
        View[] view = {etLatitude, etLongitude};
        if (Helper.isEmptyFieldValidation(view)) {
            String content = "Location:\nLatitude: " + Helper.getStringFromInput(etLatitude) +
                    "\nLongitude: " + Helper.getStringFromInput(etLongitude);

            Bitmap bitmap = Helper.textToImageEncode(context, content);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap); // convert Bitmap to byte[]
                QRCode qrCode = new QRCode();
                qrCode.setContent(content);
                qrCode.setType("Location");
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
                    Helper.makeSnackBar(rlLocationActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlLocationActivity, "Error generating QR code");
            }
        }
    }
}