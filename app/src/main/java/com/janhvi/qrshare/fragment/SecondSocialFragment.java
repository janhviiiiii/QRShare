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
import com.janhvi.qrshare.utility.Helper;

public class SecondSocialFragment extends Fragment implements View.OnClickListener {

    private TextInputEditText etInstagramLink, etYoutubeVideoId, etFacebookLink;
    private AppCompatButton btnSubmit;
    private RelativeLayout rootLayout;
    private Context context;
    private DbHelper dbHelper;
    private String socialType = "na";

    public SecondSocialFragment(String type) {
        super(R.layout.fragment_second_social); // layout file defined below
        this.socialType = type;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI(view);
        initListeners();
        initObj();
    }

    private void initUI(View view) {
        etInstagramLink = view.findViewById(R.id.etInstagramLink);
        etYoutubeVideoId = view.findViewById(R.id.etYoutubeVideoId);
        etFacebookLink = view.findViewById(R.id.etFacebookLink);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        rootLayout = view.findViewById(R.id.rlSecondSocialFragment);

        setEditTextVisibility();
    }

    private void setEditTextVisibility() {
        if (socialType.equals(Constants.INSTAGRAM)) {
            etInstagramLink.setVisibility(View.VISIBLE);
            etYoutubeVideoId.setVisibility(View.GONE);
            etFacebookLink.setVisibility(View.GONE);
        } else if (socialType.equals(Constants.YOUTUBE)) {
            etInstagramLink.setVisibility(View.GONE);
            etYoutubeVideoId.setVisibility(View.VISIBLE);
            etFacebookLink.setVisibility(View.GONE);
        } else if (socialType.equals(Constants.FACEBOOK)) {
            etInstagramLink.setVisibility(View.GONE);
            etYoutubeVideoId.setVisibility(View.GONE);
            etFacebookLink.setVisibility(View.VISIBLE);
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
        if (Helper.isEmptyFieldValidation(new View[]{etInstagramLink})) {
            String content = Helper.getStringFromInput(etInstagramLink).trim();

            if (!content.startsWith("http://") && !content.startsWith("https://")) {
                content = "https://" + content; // prepend if missing
            }

            try {
                Bitmap bitmap = Helper.textToImageEncode(context, content);
                if (bitmap != null) {
                    byte[] imageBytes = Helper.bitmapToByteArray(bitmap);
                    QRCode qrCode = new QRCode();
                    qrCode.setContent(content);
                    qrCode.setType("Instagram Link");
                    qrCode.setDate(Helper.getCurrentDate());
                    qrCode.setTime(Helper.getCurrentTime());
                    qrCode.setImage(imageBytes);
                    qrCode.setIsFavorite(0);

                    long result = dbHelper.addOrUpdateQRCode(qrCode);
                    qrCode.setQid(result);
                    if (result != -1) {
                        Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                    } else {
                        Helper.makeSnackBar(rootLayout, "Failed to generate QR Code");
                    }
                } else {
                    Helper.makeSnackBar(rootLayout, "Error generating QR code");
                }
            } catch (WriterException e) {
                e.printStackTrace();
                Helper.makeSnackBar(rootLayout, "Exception: " + e.getMessage());
            }
        }
    }

    private void generateYoutubeQRCode() {
    }

    private void generateFacebookQRCode() {
    }

}
