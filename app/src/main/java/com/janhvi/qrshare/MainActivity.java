package com.janhvi.qrshare;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.janhvi.qrshare.activity.DashboardActivity;
import com.janhvi.qrshare.utility.Helper;
import com.janhvi.qrshare.utility.SharedPref;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private RelativeLayout rlMainActivity;
    private AppCompatButton btnSubmit;
    private TextView tvLoginRedirect;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForLoginStatusAndNavigate();

    }

    private void checkForLoginStatusAndNavigate() {
        boolean loginStatus = SharedPref.getIsLoggedIn(this);

        if (loginStatus) {
            Helper.goTo(this, DashboardActivity.class);
            finish();
        } else {
            // force logout if token expired
            SharedPref.setIsLoggedIn(this, false);
            setContentView(R.layout.activity_main);
            initToolbar();
            initUI();
            initObj();
            initListener();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Register");
    }

    private void initUI() {
        rlMainActivity = findViewById(R.id.rlMainActivity);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
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
        Helper.goToAndFinish(MainActivity.this, RegisterActivity.class);
    }

    private void onClickLoginRedirect() {
        Helper.goToAndFinish(MainActivity.this, LoginActivity.class);

    }

}