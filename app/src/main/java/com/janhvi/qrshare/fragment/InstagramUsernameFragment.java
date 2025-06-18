package com.janhvi.qrshare.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.activity.QRCodeActivity;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

public class InstagramUsernameFragment extends Fragment {
    private TextInputEditText etUsername;
    private AppCompatButton btnGenerate;
    private RelativeLayout rootLayout;
    private Context context;
    private DbHelper dbHelper;

    public InstagramUsernameFragment() {
        super(R.layout.fragment_instagram_username); // define this layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etUsername = view.findViewById(R.id.etUsername);
        btnGenerate = view.findViewById(R.id.btnSubmit);
        rootLayout = view.findViewById(R.id.rlFragmentInstagramUsername);

        context = getContext();
        dbHelper = new DbHelper(context);

        btnGenerate.setOnClickListener(v -> generateQRCode());
    }

    private void generateQRCode() {
        if (Helper.isEmptyFieldValidation(new View[]{etUsername})) {
            String username = Helper.getStringFromInput(etUsername).trim();
            String content = "https://instagram.com/" + username;

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
}
