package com.janhvi.qrshare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.fragment.GenerateFragment;
import com.janhvi.qrshare.utility.DialogUtils;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = DashboardActivity.class.getSimpleName();
    private RelativeLayout rlDashboardActivity;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initToolbar();
        initUI();
        initObj();
        initListener();
        loadDefaultFragment(savedInstanceState);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuProfile) {
//            dialog = new AlertViewOrUpdateProfileDialog(context)
//                    .openProfileDialog();
            return true;
        } else if (id == R.id.menuChangePassword) {
//            dialog = new AlertChangePasswordDialog(context)
//                    .openChangePasswordDialog();
        } else if (id == R.id.menuLogout) {
            AlertDialog dialog = DialogUtils.logoutDialog(context);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initUI() {
        rlDashboardActivity = findViewById(R.id.rlMainActivity);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void initObj() {
        context = this;
    }

    private void initListener() {
        // Set item selected listener for bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String title = "";

                int id = item.getItemId();
                if (id == R.id.menuGenerate) {
                    selectedFragment = new GenerateFragment();
                    title = "Generate";
                } else if (id == R.id.menuScan) {
//                    selectedFragment = new ScanFragment();
                    title = "Scan";
                } else if (id == R.id.menuHistory) {
//                    selectedFragment = new HistoryFragment();
                    title = "History";
                } else if (id == R.id.menuFavorite) {
//                    selectedFragment = new FavoriteFragment();
                    title = "Favorite";
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment, title);
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flMainContainer, fragment);
        transaction.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void loadDefaultFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Fragment defaultFragment = new GenerateFragment();
            String defaultTitle = "Generate";

            loadFragment(defaultFragment, defaultTitle);
            bottomNavigationView.setSelectedItemId(R.id.menuGenerate);
        }
    }

}