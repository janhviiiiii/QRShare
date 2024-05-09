package com.example.qrshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.qrshare.databinding.ActivityNavigationBarBinding;

public class navigation_bar extends AppCompatActivity {

    ActivityNavigationBarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new Generate_Fragment()); //default fragment will be Generate
        binding.bottomNavigationView.setBackground(null); //for transparent bg

        binding.bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {

            //to switch between Fragments
            switch (item.getItemId()) {
                case R.id.generate:
                    replaceFragment(new Generate_Fragment());
                    break;

                case R.id.scan:
                    replaceFragment(new Scan_Fragment());
                    break;

                case R.id.history:
                    replaceFragment(new History_Fragment());
                    break;

                case R.id.favorite:
                    replaceFragment(new Favorite_Fragment());
                    break;

                case R.id.settings:
                    replaceFragment(new Settings_Fragment());
                    break;
            }

            return true;
        });
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment); //only frag will change bottom nav will be as it is
        fragmentTransaction.commit();
    }
}