package com.example.qrshare;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView text;

    ViewPager2 viewPager2;
    ArrayList<viewpageItem> viewPagerItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //for image slider

        //find the ImageSlider
        ImageSlider imageSlider = findViewById(R.id.image_slider);
        // Creating a list of SlideModel objects
        ArrayList<SlideModel> sliderModels = new ArrayList<>();
        // Adding slides with heading and description
        sliderModels.add(new SlideModel(R.drawable.generate, "Generate QR Codes", ScaleTypes.FIT)); //"Heading 1",
        sliderModels.add(new SlideModel(R.drawable.scan,"Scan QR Codes", ScaleTypes.FIT));
        sliderModels.add(new SlideModel(R.drawable.favorite,"Add to Favorite",  ScaleTypes.FIT));
        sliderModels.add(new SlideModel(R.drawable.history, "Add to History",  ScaleTypes.FIT));
        // Set the list of SlideModel objects to the ImageSlider
        imageSlider.setImageList(sliderModels, ScaleTypes.FIT);



        //Signup Button click
        button=(Button) findViewById(R.id.btn_signup);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openSignupPage();
            }


        });


        //txt_login text click
        text = (TextView) findViewById(R.id.txt_login);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPage();
            }
        });
    }

    //method of openSignupPage
    public void  openSignupPage()
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    //method of openLoginPage
    public void  openLoginPage()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}