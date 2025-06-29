package com.janhvi.qrshare;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private ImageSwitcher image_slider;
    private int[] imageResources = {
            R.drawable.ic_qrcode,
            R.drawable.ic_favorite_border,
            R.drawable.ic_history
    };
    private int currentImageIndex = 0;
    private Handler imageSwitcherHandler;
    private Runnable imageSwitcherRunnable;


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
        image_slider = findViewById(R.id.image_slider);

        image_slider.setFactory(() -> {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return imageView;
        });

        image_slider.setImageResource(imageResources[0]); // first image
        startImageSwitcher(); // safe to call here
    }

    private void startImageSwitcher() {
        imageSwitcherHandler = new Handler();
        imageSwitcherRunnable = new Runnable() {
            @Override
            public void run() {
                currentImageIndex = (currentImageIndex + 1) % imageResources.length;
                image_slider.setImageResource(imageResources[currentImageIndex]);
                imageSwitcherHandler.postDelayed(this, 3000); // every 3 seconds
            }
        };
        imageSwitcherHandler.postDelayed(imageSwitcherRunnable, 3000);
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
    @Override
    protected void onDestroy() {
        if (imageSwitcherHandler != null && imageSwitcherRunnable != null) {
            imageSwitcherHandler.removeCallbacks(imageSwitcherRunnable);
        }
        super.onDestroy();
    }
}