package com.janhvi.qrshare;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.User;
import com.janhvi.qrshare.utility.Helper;
import com.janhvi.qrshare.utility.SharedPref;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RegisterActivity.class.getSimpleName();
    private RelativeLayout rlRegisterActivity;
    private TextInputEditText etEmail, etUsername, etPassword, etConfirmPassword;
    private TextView tvLoginRedirect;
    private AppCompatButton btnSubmit;
    private Context context;
    private User entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initToolbar();
        initUI();
        initObj();
        initListener();

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Register");
    }

    private void initUI() {
        rlRegisterActivity = findViewById(R.id.rlRegisterActivity);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
        entity = new User();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        btnSubmit.setOnClickListener(this);
        tvLoginRedirect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit) {
            onClickBtnSubmit();
        } else if (id == R.id.tvLoginRedirect) {
            onClickLoginRedirect();
        }
    }

    private void onClickBtnSubmit() {
        View view[] = {etEmail, etUsername, etPassword, etConfirmPassword};
        if (Helper.isEmptyFieldValidation(view) && isValidated()) {
            setUserEntity();
            long uid = dbHelper.addOrUpdateUser(entity);
            if (uid > 0) {
                Helper.makeSnackBar(rlRegisterActivity, "User registration successful");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Helper.goToAndFinish(RegisterActivity.this, LoginActivity.class);
                    }
                }, 1000); // 2000 milliseconds = 2 seconds
            }
        }

    }

    private boolean isValidated() {
        if (Helper.isPasswordValid(etPassword)) {
            return true;
        }
        if (Objects.equals(Helper.getStringFromInput(etPassword), Helper.getStringFromInput(etConfirmPassword))) {
            return true;
        } else {
            Helper.makeSnackBar(rlRegisterActivity, "Password and Confirm Password do not match");
        }
        return false;
    }

    private void setUserEntity() {
        entity.setEmail(Helper.getStringFromInput(etEmail));
        entity.setUsername(Helper.getStringFromInput(etUsername));
        entity.setPassword(Helper.getStringFromInput(etPassword));
    }

    private void onClickLoginRedirect() {
        Helper.goToAndFinish(RegisterActivity.this, LoginActivity.class);
    }

}