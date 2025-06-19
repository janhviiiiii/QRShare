package com.janhvi.qrshare.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.adapter.FacebookPagerAdapter;
import com.janhvi.qrshare.adapter.InstagramPagerAdapter;
import com.janhvi.qrshare.adapter.YoutubePagerAdapter;
import com.janhvi.qrshare.utility.Constants;

import java.util.function.LongToDoubleFunction;

public class SocialActivity extends AppCompatActivity {
    public static final String TAG = SocialActivity.class.getSimpleName();
    private ImageView ivInstagram, ivYoutube, ivFacebook;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private InstagramPagerAdapter instagramPagerAdapter;
    private YoutubePagerAdapter youtubePagerAdapter;
    private FacebookPagerAdapter facebookPagerAdapter;
    private String socialType = "na";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        loadIntentData();
        initToolbar();
        initUI();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Instagram");
    }

    private void loadIntentData() {
        socialType = getIntent().getStringExtra(Constants.SOCIAL_TYPE);
    }

    private void initUI() {
        ivInstagram = findViewById(R.id.ivInstagram);
        ivYoutube = findViewById(R.id.ivYoutube);
        ivFacebook = findViewById(R.id.ivFacebook);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        loadTabs();

//        new TabLayoutMediator(tabLayout, viewPager,
//                (tab, position) -> {
//                    if (position == 0)
//                        tab.setText("Username");
//                    else
//                        tab.setText("Profile Link");
//                }
//        ).attach();
    }

    private void loadTabs() {
        if (socialType.equals(Constants.INSTAGRAM)) {
            instagramPagerAdapter = new InstagramPagerAdapter(this);
            viewPager.setAdapter(instagramPagerAdapter);
            loadInstagramTabs();
        } else if (socialType.equals(Constants.YOUTUBE)) {
            youtubePagerAdapter = new YoutubePagerAdapter(this);
            viewPager.setAdapter(youtubePagerAdapter);
            loadYoutubeTabs();
        } else if (socialType.equals(Constants.FACEBOOK)) {
            facebookPagerAdapter = new FacebookPagerAdapter(this);
            viewPager.setAdapter(facebookPagerAdapter);
            loadFacebookTabs();
        }
    }

    private void loadInstagramTabs() {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText("Instagram Username");
                    else
                        tab.setText("Profile Link");
                }
        ).attach();
    }

    private void loadYoutubeTabs() {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText("Youtube Link");
                    else
                        tab.setText("Youtube Video Id");
                }
        ).attach();
    }

    private void loadFacebookTabs() {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText("Facebook Username");
                    else
                        tab.setText("Facebook profile link");
                }
        ).attach();
    }
}