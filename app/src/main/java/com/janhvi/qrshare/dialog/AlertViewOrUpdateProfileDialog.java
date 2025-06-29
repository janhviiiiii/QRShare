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
import com.janhvi.qrshare.model.User;

import com.janhvi.qrshare.utility.Helper;
import com.janhvi.qrshare.utility.SharedPref;


public class AlertViewOrUpdateProfileDialog implements View.OnClickListener {
    private static final String TAG = AlertViewOrUpdateProfileDialog.class.getSimpleName();
    private View alertView;
    private TextInputEditText etUsername, etEmail;
    private AppCompatButton btnCancel, btnUpdate;
    private Dialog dialog;
    private Context context;
    private User entity;
    private DbHelper dbHelper;

    public AlertViewOrUpdateProfileDialog(Context context) {
        this.context = context;
        this.entity = new User();
        this.dbHelper = new DbHelper(context);
    }

    public Dialog openProfileDialog() {
        try {
            try {
                LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
                alertView = layoutInflater.inflate(R.layout.alert_dialog_profile, null);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                if (alertView.getParent() != null) {
                    ((ViewGroup) alertView.getParent()).removeView(alertView);
                }
                alertBuilder.setView(alertView);

                initUI();
                setListeners();
                dialog = alertBuilder.create();

                //get user profile
                getProfile();

            } catch (Exception e) {
                Log.e(TAG, "Error in AlertProfileDialog: ", e);
            }

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error in AlertProfileDialog: ", e);
        }
        return dialog;
    }

    private void setDataToText() {
        try {
            if (entity != null) {
                etUsername.setText(entity.getUsername());
                etEmail.setText(entity.getEmail());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setDataToText: ", e);
        }
    }

    private void getProfile() {
        entity.setUsername(SharedPref.getUserName(context));
        entity.setEmail(SharedPref.getUserEmail(context));
        setDataToText();
    }

    private void initUI() {
        etUsername = alertView.findViewById(R.id.etUsername);
        etEmail = alertView.findViewById(R.id.etEmail);
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
        View[] views = {etUsername, etEmail};
        if (Helper.isEmptyFieldValidation(views) && Helper.isEmailValid(etEmail)) {
            setInputDataToEntity();
            String uuid = SharedPref.getUserUid(context);
            User user = dbHelper.getUserByUid(Long.parseLong(uuid));
            if (user != null) {
                user.setUsername(Helper.getStringFromInput(etUsername));
                user.setEmail(Helper.getStringFromInput(etEmail));
                user.setPassword(user.getPassword());
                dbHelper.addOrUpdateUser(user);

                // Update shared preferences too
                SharedPref.setUsername(context, user.getUsername());
                SharedPref.setUserEmail(context, user.getEmail());

                Helper.makeSnackBar(etUsername, "Profile updated successfully");

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } else {
                Helper.makeSnackBar(etUsername, "User not found");
            }


        }
    }

    private void setInputDataToEntity() {
        entity.setUsername(Helper.getStringFromInput(etUsername));
        entity.setEmail(Helper.getStringFromInput(etEmail));
    }
}