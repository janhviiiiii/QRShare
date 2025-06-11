package com.janhvi.qrshare.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;


public class CopyActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = ContactActivity.class.getSimpleName();
    private RelativeLayout rlCopyActivity;
    private TextInputEditText etCopyText;
    private AppCompatButton btnSubmit;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);

        initToolbar();
        initUI();
        initObj();
        initListener();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Copy from Clipboard");
    }

    private void initUI() {
        rlCopyActivity = findViewById(R.id.rlCopyActivity);
        etCopyText = findViewById(R.id.etCopyText);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
        entity = new QRCode();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        btnSubmit.setOnClickListener(this);
        etCopyText.setOnClickListener(this);
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
        } else if (id == R.id.etCopyText) {
            onClickCopyText();
        }
    }

    private void onClickCopyText() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String clipboardText = item.getText().toString();
            etCopyText.setText(clipboardText);

            Helper.makeSnackBar(rlCopyActivity, "Text Pasted");
        } else {
            Helper.makeSnackBar(rlCopyActivity, "No plain text found in clipboard");
        }
    }

    private void onClickBtnSubmit() throws WriterException {
        View[] view = {etCopyText};
        if (Helper.isEmptyFieldValidation(view)) {
            String content = "Copied Text:\n: " + Helper.getStringFromInput(etCopyText);
            Bitmap bitmap = Helper.textToImageEncode(context, content);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap); // convert Bitmap to byte[]
                QRCode qrCode = new QRCode();
                qrCode.setContent(content);
                qrCode.setType("Copy from Clipboard");
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
                    Helper.makeSnackBar(rlCopyActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlCopyActivity, "Error generating QR code");
            }
        }
    }

}