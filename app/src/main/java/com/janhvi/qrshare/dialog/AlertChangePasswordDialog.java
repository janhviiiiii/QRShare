package com.janhvi.qrshare.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.utility.Helper;
import com.janhvi.qrshare.utility.SharedPref;

public class AlertChangePasswordDialog implements View.OnClickListener {
    private static final String TAG = AlertChangePasswordDialog.class.getSimpleName();
    private View alertView;
    private TextInputEditText etOldPassword, etNewPassword;
    private AppCompatButton btnCancel, btnUpdate;
    private Dialog dialog;
    private Context context;
    private DbHelper dbHelper;

    public AlertChangePasswordDialog(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }

    public Dialog openChangePasswordDialog() {
        try {
            try {
                LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
                alertView = layoutInflater.inflate(R.layout.alert_dialog_change_password, null);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                if (alertView.getParent() != null) {
                    ((ViewGroup) alertView.getParent()).removeView(alertView);
                }
                alertBuilder.setView(alertView);

                initUI();
                setListeners();
                dialog = alertBuilder.create();

            } catch (Exception e) {
                Log.e(TAG, "Error in AlertChangePasswordDialog: ", e);
            }

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error in AlertChangePasswordDialog: ", e);
        }
        return dialog;
    }

    private void initUI() {
        etOldPassword = alertView.findViewById(R.id.etOldPassword);
        etNewPassword = alertView.findViewById(R.id.etNewPassword);
        btnCancel = alertView.findViewById(R.id.btnCancel);
        btnUpdate = alertView.findViewById(R.id.btnUpdate);
    }

    private void setListeners() {
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnCancel) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } else if (id == R.id.btnUpdate) {
            onClickBtnUpdate();
        }
    }

    private void onClickBtnUpdate() {
        View[] views = {etOldPassword, etNewPassword};
        if (Helper.isEmptyFieldValidation(views) && Helper.isPasswordValid(etNewPassword)) {
            String username = SharedPref.getUserName(context);
            String oldPassword = Helper.getStringFromInput(etOldPassword);
            String newPassword = Helper.getStringFromInput(etNewPassword);
            String uuid = SharedPref.getUserUid(context);

            boolean isUpdated = dbHelper.updatePassword(Long.parseLong(uuid), oldPassword, newPassword);

            if (isUpdated) {
                Helper.makeSnackBar(etOldPassword, "Password updated successfully");
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
            } else {
                Helper.makeSnackBar(etOldPassword, "Old password is incorrect");
            }
        }
    }
}
