package com.janhvi.qrshare;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.janhvi.qrshare.activity.DashboardActivity;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.User;
import com.janhvi.qrshare.utility.Helper;
import com.janhvi.qrshare.utility.SharedPref;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = LoginActivity.class.getSimpleName();
    private RelativeLayout rlLoginActivity;
    private TextInputEditText etEmail, etPassword;
    private TextView tvRegisterRedirect;
    private AppCompatButton btnSubmit;
    private Context context;
    private User entity;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        rlLoginActivity = findViewById(R.id.rlLoginActivity);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvRegisterRedirect = findViewById(R.id.tvRegisterRedirect);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
        entity = new User();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        btnSubmit.setOnClickListener(this);
        tvRegisterRedirect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit) {
            onClickBtnSubmit();
        } else if (id == R.id.tvRegisterRedirect) {
            onClickRegisterRedirect();
        }
    }

    private void onClickBtnSubmit() {
        View view[] = {etEmail, etPassword};
        if (Helper.isEmptyFieldValidation(view)) {
            String password = Helper.getStringFromInput(etPassword);
            String email = Helper.getStringFromInput(etEmail);
            User user = dbHelper.userLogin(email, password);
            if (user != null) {
                Helper.makeSnackBar(rlLoginActivity, "Login successful\nWelcome " + user.getUsername());
                SharedPref.setIsLoggedIn(context, true);
                SharedPref.setUuid(context, String.valueOf(user.getUid())); // If you store user data in shared pref
                SharedPref.setUserEmail(context, user.getEmail());
                SharedPref.setUsername(context, user.getUsername());
                Helper.goToAndFinish(this, DashboardActivity.class); // or your next screen
            } else {
                Helper.makeSnackBar(rlLoginActivity, "Invalid email or password");
            }
        }
    }


    private void setUserEntity() {
        entity.setEmail(Helper.getStringFromInput(etEmail));
        entity.setPassword(Helper.getStringFromInput(etPassword));
    }

    private void onClickRegisterRedirect() {
        Helper.goToAndFinish(LoginActivity.this, RegisterActivity.class);
    }

}