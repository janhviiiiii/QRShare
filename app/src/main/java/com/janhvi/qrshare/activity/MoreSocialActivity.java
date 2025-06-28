package com.janhvi.qrshare.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.hbb20.CountryCodePicker;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.DialogUtils;
import com.janhvi.qrshare.utility.Helper;

public class MoreSocialActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MoreSocialActivity.class.getSimpleName();
    private RelativeLayout rlMoreSocialActivity;
    private LinearLayout llWhatsapp;
    private CountryCodePicker countryCodePicker;
    private ImageView ivLinkedin, ivWhatsapp, ivSpotify, ivPinterest, ivSnapchat, ivX;
    private TextInputEditText etLinkedinProfileLink, etWhatsappNo, etSpotifyArtistName, etSpotifySongName, etPinterestUsername, etSnapchatUsername, etXUsername;
    private AppCompatButton btnSubmit;
    private Context context;
    private DbHelper dbHelper;
    private String socialType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_social);
        loadIntentData();
        initToolbar();
        initUI();
        initObj();
        initListener();
    }

    private void loadIntentData() {
        socialType = getIntent().getStringExtra(Constants.SOCIAL_TYPE);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        switch (socialType) {
            case Constants.LINKEDIN:
                toolbar.setTitle("Linkedin");
                break;
            case Constants.WHATSAPP:
                toolbar.setTitle("WhatsApp");
                break;
            case Constants.SPOTIFY:
                toolbar.setTitle("Spotify");
                break;
            case Constants.PINTEREST:
                toolbar.setTitle("Pinterest");
                break;
            case Constants.SNAPCHAT:
                toolbar.setTitle("Snapchat");
                break;
            case Constants.X:
                toolbar.setTitle("X");
                break;
        }
    }

    private void initUI() {
        rlMoreSocialActivity = findViewById(R.id.rlMoreSocialActivity);
        ivLinkedin = findViewById(R.id.ivLinkedin);
        ivWhatsapp = findViewById(R.id.ivWhatsapp);
        ivSpotify = findViewById(R.id.ivSpotify);
        ivPinterest = findViewById(R.id.ivPinterest);
        ivSnapchat = findViewById(R.id.ivSnapchat);
        ivX = findViewById(R.id.ivX);
        etLinkedinProfileLink = findViewById(R.id.etLinkedinProfileLink);
        etWhatsappNo = findViewById(R.id.etWhatsappNo);
        etSpotifyArtistName = findViewById(R.id.etSpotifyArtistName);
        etSpotifySongName = findViewById(R.id.etSpotifySongName);
        etPinterestUsername = findViewById(R.id.etPinterestUsername);
        etSnapchatUsername = findViewById(R.id.etSnapchatUsername);
        etXUsername = findViewById(R.id.etXUsername);
        llWhatsapp = findViewById(R.id.llWhatsapp);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        btnSubmit = findViewById(R.id.btnSubmit);

        loadUI();
    }

    private void loadUI() {
        switch (socialType) {
            case Constants.LINKEDIN:
                ivLinkedin.setVisibility(View.VISIBLE);
                etLinkedinProfileLink.setVisibility(View.VISIBLE);
                break;
            case Constants.WHATSAPP:
                ivWhatsapp.setVisibility(View.VISIBLE);
                llWhatsapp.setVisibility(View.VISIBLE);
                break;
            case Constants.SPOTIFY:
                ivSpotify.setVisibility(View.VISIBLE);
                etSpotifyArtistName.setVisibility(View.VISIBLE);
                etSpotifySongName.setVisibility(View.VISIBLE);
                break;
            case Constants.PINTEREST:
                ivPinterest.setVisibility(View.VISIBLE);
                etPinterestUsername.setVisibility(View.VISIBLE);
                break;
            case Constants.SNAPCHAT:
                ivSnapchat.setVisibility(View.VISIBLE);
                etSnapchatUsername.setVisibility(View.VISIBLE);
                break;
            case Constants.X:
                ivX.setVisibility(View.VISIBLE);
                etXUsername.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initObj() {
        context = this;
//        entity = new QRCode();
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
        switch (socialType) {
            case Constants.LINKEDIN:
                generateLinkedinQRCode();
                break;
            case Constants.WHATSAPP:
                generateWhatsappQRCode();
                break;
            case Constants.SPOTIFY:
                generateSpotifyQRCode();
                break;
            case Constants.PINTEREST:
                generatePinterestQRCode();
                break;
            case Constants.SNAPCHAT:
                generateSnapchatQRCode();
                break;
            case Constants.X:
                generateXQRCode();
                break;
        }

    }

    private void generateLinkedinQRCode() {
        View[] view = {etLinkedinProfileLink};
        if (Helper.isEmptyFieldValidation(view)) {
            String content = Helper.getStringFromInput(etLinkedinProfileLink).trim();

            if (!content.startsWith("http://") && !content.startsWith("https://")) {
                content = "https://" + content;
            }

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");
            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("LinkedIn Profile");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);

                    DialogUtils.dismissDialog();

                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rlMoreSocialActivity, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rlMoreSocialActivity, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rlMoreSocialActivity, "Exception: " + e.getMessage());
            }
        }
    }

    private void generateWhatsappQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etWhatsappNo})) {
            String code = countryCodePicker.getSelectedCountryCode();
            String phone = Helper.getStringFromInput(etWhatsappNo).trim();
            String content = "https://wa.me/" + code + phone;

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");
            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("WhatsApp Number");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);

                    DialogUtils.dismissDialog();

                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rlMoreSocialActivity, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rlMoreSocialActivity, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rlMoreSocialActivity, "Exception: " + e.getMessage());
            }
        }
    }

    private void generateSpotifyQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etSpotifyArtistName, etSpotifySongName})) {
            String artist = Helper.getStringFromInput(etSpotifyArtistName).trim().replace(" ", "%20");
            String song = Helper.getStringFromInput(etSpotifySongName).trim().replace(" ", "%20");
            String content = "https://open.spotify.com/search/" + artist + "%20" + song;

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");
            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("Spotify Song");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);

                    DialogUtils.dismissDialog();

                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rlMoreSocialActivity, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rlMoreSocialActivity, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rlMoreSocialActivity, "Exception: " + e.getMessage());
            }
        }
    }

    private void generatePinterestQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etPinterestUsername})) {
            String username = Helper.getStringFromInput(etPinterestUsername).trim();
            String content = "https://www.pinterest.com/" + username;

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");
            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("Pinterest Username");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);

                    DialogUtils.dismissDialog();

                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rlMoreSocialActivity, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rlMoreSocialActivity, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rlMoreSocialActivity, "Exception: " + e.getMessage());
            }
        }
    }

    private void generateSnapchatQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etSnapchatUsername})) {
            String username = Helper.getStringFromInput(etSnapchatUsername).trim();
            String content = "https://www.snapchat.com/add/" + username;

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");
            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("Snapchat Username");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);

                    DialogUtils.dismissDialog();

                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rlMoreSocialActivity, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rlMoreSocialActivity, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rlMoreSocialActivity, "Exception: " + e.getMessage());
            }
        }
    }

    private void generateXQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etXUsername})) {
            String username = Helper.getStringFromInput(etXUsername).trim();
            String content = "https://x.com/" + username;

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");
            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("X Username");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);

                    DialogUtils.dismissDialog();

                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rlMoreSocialActivity, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rlMoreSocialActivity, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rlMoreSocialActivity, "Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}