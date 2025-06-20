package com.janhvi.qrshare.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.activity.QRCodeActivity;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.DialogUtils;
import com.janhvi.qrshare.utility.Helper;

public class FirstSocialFragment extends Fragment implements View.OnClickListener {
    private TextInputEditText etInstagramUsername, etYoutubeLink, etFacebookUsername;
    private AppCompatButton btnSubmit;
    private RelativeLayout rootLayout;
    private Context context;
    private DbHelper dbHelper;
    private String socialType;

    public FirstSocialFragment(String type) {
        super(R.layout.fragment_first_social); // define this layout
        this.socialType = type;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initUI(view);
        initListeners();
        initObj();
    }

    private void initUI(View view) {
        etInstagramUsername = view.findViewById(R.id.etInstagramUsername);
        etYoutubeLink = view.findViewById(R.id.etYoutubeLink);
        etFacebookUsername = view.findViewById(R.id.etFacebookUsername);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        rootLayout = view.findViewById(R.id.rlFirstSocialFragment);

        setEditTextVisibility();
    }

    private void setEditTextVisibility() {
        if (socialType.equals(Constants.INSTAGRAM)) {
            etInstagramUsername.setVisibility(View.VISIBLE);
            etYoutubeLink.setVisibility(View.GONE);
            etFacebookUsername.setVisibility(View.GONE);
        } else if (socialType.equals(Constants.YOUTUBE)) {
            etInstagramUsername.setVisibility(View.GONE);
            etYoutubeLink.setVisibility(View.VISIBLE);
            etFacebookUsername.setVisibility(View.GONE);
        } else if (socialType.equals(Constants.FACEBOOK)) {
            etInstagramUsername.setVisibility(View.GONE);
            etYoutubeLink.setVisibility(View.GONE);
            etFacebookUsername.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        btnSubmit.setOnClickListener(this);
    }

    private void initObj() {
        context = getContext();
        dbHelper = new DbHelper(context);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit && socialType.equals(Constants.INSTAGRAM)) {
            generateInstagramQRCode();
        } else if (id == R.id.btnSubmit && socialType.equals(Constants.YOUTUBE)) {
            generateYoutubeQRCode();
        } else if (id == R.id.btnSubmit && socialType.equals(Constants.FACEBOOK)) {
            generateFacebookQRCode();
        }
    }

    private void generateInstagramQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etInstagramUsername})) {
            String username = Helper.getStringFromInput(etInstagramUsername).trim();
            String content = "https://instagram.com/" + username;

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");

            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("Instagram Username");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);

                    // Dismiss loading
                    DialogUtils.dismissDialog();

                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rootLayout, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rootLayout, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rootLayout, "Exception: " + e.getMessage());
            }
        }
    }

    private void generateYoutubeQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etYoutubeLink})) {
            String content = Helper.getStringFromInput(etYoutubeLink).trim();

            if (!content.startsWith("http://") && !content.startsWith("https://")) {
                content = "https://" + content; // ensure it's a valid URL
            }

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");

            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("YouTube Link");
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
                        Helper.makeSnackBar(rootLayout, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rootLayout, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rootLayout, "Exception: " + e.getMessage());
            }
        }
    }


    private void generateFacebookQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etFacebookUsername})) {
            String username = Helper.getStringFromInput(etFacebookUsername).trim();
            String content = "https://facebook.com/" + username;

            // Show loading
            DialogUtils.showLoadingDialog(context, "Generating QR Code...");

            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("Facebook Username");
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
                        Helper.makeSnackBar(rootLayout, "Failed to generate QR Code");
                    }
                } else {
                    DialogUtils.dismissDialog();
                    Helper.makeSnackBar(rootLayout, "Error generating QR code");
                }
            } catch (WriterException e) {
                DialogUtils.dismissDialog();
                e.printStackTrace();
                Helper.makeSnackBar(rootLayout, "Exception: " + e.getMessage());
            }
        }
    }
}
