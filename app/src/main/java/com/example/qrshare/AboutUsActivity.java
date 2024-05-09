package com.example.qrshare;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        // Set the initial title in onCreate or wherever needed
        getSupportActionBar().setTitle("About US");
    }



}