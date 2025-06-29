package com.janhvi.qrshare.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = QRCodeActivity.class.getSimpleName();
    private RelativeLayout rlQRCodeActivity;
    private ImageView ivQrCode;
    private TextView tvScannedContent;
    private MaterialButton btnShare, btnCopyToClipboard, btnSearch, btnDownload, btnFavorite;
    private Context context;
    private DbHelper dbHelper;
    private QRCode entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initToolbar();
        initUI();
        initObj();
        initListener();
        loadIntentData();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("QR Code");
    }

    private void initUI() {
        rlQRCodeActivity = findViewById(R.id.rlQRCodeActivity);
        tvScannedContent = findViewById(R.id.tvScannedContent);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnSearch = findViewById(R.id.btnSearch);
        btnCopyToClipboard = findViewById(R.id.btnCopyToClipboard);
        btnDownload = findViewById(R.id.btnDownload);
        btnShare = findViewById(R.id.btnShare);
        ivQrCode = findViewById(R.id.ivQrCode);
    }

    private void initObj() {
        context = this;
//        entity = new QRCode();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        btnDownload.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnCopyToClipboard.setOnClickListener(this);
        btnFavorite.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void loadIntentData() {
        entity = (QRCode) getIntent().getSerializableExtra(Constants.QRCODE);

        if (entity != null) {
            Log.d(TAG, entity.getType() + "  " + entity.getContent() + "   " + entity.getQid());

            byte[] imageBytes = entity.getImage();
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ivQrCode.setImageBitmap(bitmap);
            }
            boolean isHistoryView = getIntent().getBooleanExtra(String.valueOf(Constants.IS_HISTORY_VIEW), false);

            if (isHistoryView  || entity.getType().equalsIgnoreCase(Constants.SCANNED)) {
                btnCopyToClipboard.setVisibility(VISIBLE);
                btnDownload.setVisibility(GONE);
                tvScannedContent.setVisibility(VISIBLE);
                tvScannedContent.setText("QR Code CONTENT: \n" + entity.getContent());
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnDownload) {
            onClickBtnDownload();
        } else if (id == R.id.btnShare) {
            onClickBtnShare();
        } else if (id == R.id.btnSearch) {
            onClickBtnSearch();
        } else if (id == R.id.btnFavorite) {
            onClickBtnFavorite();
        } else if (id == R.id.btnCopyToClipboard) {
            onClickBtnCopy();
        }
    }

    private void onClickBtnDownload() {
        ivQrCode.setDrawingCacheEnabled(true);
        Bitmap bitmap = ivQrCode.getDrawingCache();

        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "QRCode_" + System.currentTimeMillis(),
                "QR Code Image"
        );

        if (savedImageURL != null) {
            Helper.makeSnackBar(rlQRCodeActivity, "QR Code saved to gallery");
        } else {
            Helper.makeSnackBar(rlQRCodeActivity, "Failed to save image");
        }
    }

    private void onClickBtnShare() {
        ivQrCode.setDrawingCacheEnabled(true);
        ivQrCode.buildDrawingCache(); //  Add this line
        Bitmap bitmap = Bitmap.createBitmap(ivQrCode.getDrawingCache()); // safer copy
        ivQrCode.setDrawingCacheEnabled(false); // turn off afterward

        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "qrcode.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(
                    context,
                    getPackageName() + ".provider",
                    file
            );

            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Helper.makeSnackBar(rlQRCodeActivity, "Error sharing QR Code");
        }
    }


    private void onClickBtnFavorite() {
        if (entity != null) {
            int newFavorite = entity.getIsFavorite() == 1 ? 0 : 1;
            entity.setIsFavorite(newFavorite);
            dbHelper.addOrUpdateQRCode(entity);

            Helper.makeSnackBar(rlQRCodeActivity,
                    newFavorite == 1 ? "Marked as Favorite" : "Removed from Favorites");
        }
    }

    private void onClickBtnCopy() {
        if (entity.getContent() != null || entity.getContent().isEmpty()) {
            String content = entity.getContent();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("QR Code Content", content);
            clipboard.setPrimaryClip(clip);

            Helper.makeSnackBar(rlQRCodeActivity, "Copied to clipboard");
        } else {
            Helper.makeSnackBar(rlQRCodeActivity, "No content to copy");
        }
    }

    private void onClickBtnSearch() {
        if (entity != null && entity.getContent() != null) {
            String query = entity.getContent().replace("Text: ", "");
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, query);
            startActivity(intent);
        }
    }

}