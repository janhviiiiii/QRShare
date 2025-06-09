package com.janhvi.qrshare.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

public class WebsiteActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = WebsiteActivity.class.getSimpleName();
    private RelativeLayout rlWebsiteActivity;
    private TextInputEditText etUrl;
    private ChipGroup chipGroup;
    private Chip chipHttp, chipHttps, chipWww, chipEdu;
    private AppCompatButton btnSubmit;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);
        initToolbar();
        initUI();
        initObj();
        initListener();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Website");
    }

    private void initUI() {
        rlWebsiteActivity = findViewById(R.id.rlWebsiteActivity);
        etUrl = findViewById(R.id.etUrl);
        chipGroup = findViewById(R.id.chipGroup);
        chipHttp = findViewById(R.id.chipHttp);
        chipHttps = findViewById(R.id.chipHttps);
        chipWww = findViewById(R.id.chipWww);
        chipEdu = findViewById(R.id.chipEdu);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
        entity = new QRCode();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        btnSubmit.setOnClickListener(this);
        chipHttps.setOnClickListener(this);
        chipHttp.setOnClickListener(this);
        chipWww.setOnClickListener(this);
        chipEdu.setOnClickListener(this);
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
        } else if (v instanceof Chip) {
            Chip chip = (Chip) v;
            String chipText = chip.getText().toString();
            appendToUrlField(chipText);
        }
    }

    private void appendToUrlField(String text) {
        String current = Helper.getStringFromInput(etUrl);
        if (!current.contains(text)) {
            etUrl.setText(current + text);
            etUrl.setSelection(etUrl.getText().length()); // move cursor to end
        }
    }

    private void onClickBtnSubmit() throws WriterException {
        View[] view = {etUrl};
        if (Helper.isEmptyFieldValidation(view)) {
            String rawInput = Helper.getStringFromInput(etUrl);
            String finalUrl = buildUrlFromChips("url:" + rawInput);

            Bitmap bitmap = Helper.textToImageEncode(context, finalUrl);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap);

                QRCode qrCode = new QRCode();
                qrCode.setContent(finalUrl);
                qrCode.setType("Website");
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
                    Helper.makeSnackBar(rlWebsiteActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlWebsiteActivity, "Error generating QR code");
            }
        }
    }

    private String buildUrlFromChips(String input) {
        StringBuilder urlBuilder = new StringBuilder();

        if (chipHttps.isChecked()) {
            urlBuilder.append("https://");
        } else if (chipHttp.isChecked()) {
            urlBuilder.append("http://");
        }

        if (chipWww.isChecked() && !input.startsWith("www.")) {
            urlBuilder.append("www.");
        }

        urlBuilder.append(input);

        if (chipEdu.isChecked() && !input.endsWith(".edu")) {
            urlBuilder.append(".edu");
        }

        return urlBuilder.toString();
    }

}

