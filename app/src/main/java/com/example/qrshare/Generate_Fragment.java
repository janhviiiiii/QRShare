package com.example.qrshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class Generate_Fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_generate_, container, false);



        //Text Activity
        ImageView img_text = view.findViewById(R.id.img_text);

        img_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TextActivity.class);
                startActivity(intent);
            }
        });

        //Website Activity
        ImageView img_website = view.findViewById(R.id.img_website);

        img_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebsiteActivity.class);
                startActivity(intent);
            }
        });

        //Contact Activity
        ImageView img_contact = view.findViewById(R.id.img_contact);

        img_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);

            }
        });

        //Phone Activity
        ImageView img_phone = view.findViewById(R.id.img_phone);

        img_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhoneActivity.class);
                startActivity(intent);
            }
        });

        //Email Activity
        ImageView img_email = view.findViewById(R.id.img_email);

        img_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EmailActivity.class);
                startActivity(intent);
            }
        });

        //Profile Activity
        ImageView img_profile = view.findViewById(R.id.img_profile);

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        //SMS Activity
        ImageView img_sms = view.findViewById(R.id.img_sms);

        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SMSActivity.class);
                startActivity(intent);
            }
        });

        //Event Activity
        ImageView img_event = view.findViewById(R.id.img_event);

        img_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                startActivity(intent);
            }
        });

        //Company Activity
        ImageView img_company = view.findViewById(R.id.img_company);

        img_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CompanyActivity.class);
                startActivity(intent);
            }
        });

        //Copy from Clipboard Activity
        ImageView img_copy = view.findViewById(R.id.img_copy);

        img_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CopyActivity.class);
                startActivity(intent);
            }
        });

        //Location Activity
        ImageView img_location = view.findViewById(R.id.img_location);

        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });

        //WiFi Activity
        ImageView img_wifi = view.findViewById(R.id.img_wifi);

        img_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WifiActivity.class);
                startActivity(intent);
            }
        });

        //Instagram Activity
        ImageView img_instagram = view.findViewById(R.id.img_instagram);

        img_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InstagramActivity.class);
                startActivity(intent);
            }
        });

        //Linkedin Activity
        ImageView img_linkedin = view.findViewById(R.id.img_linkedin);

        img_linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LinkedinActivity.class);
                startActivity(intent);
            }
        });

        //YouTube Activity
        ImageView img_youtube = view.findViewById(R.id.img_youtube);

        img_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), YoutubeActivity.class);
                startActivity(intent);
            }
        });

        //WhatsApp Activity
        ImageView img_whatsapp = view.findViewById(R.id.img_whatsapp);

        img_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WhatsappActivity.class);
                startActivity(intent);
            }
        });

        //Facebook Activity
        ImageView img_facebook = view.findViewById(R.id.img_facebook);

        img_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FacebookActivity.class);
                startActivity(intent);
            }
        });

        //Spotify Activity
        ImageView img_spotify = view.findViewById(R.id.img_spotify);

        img_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpotifyActivity.class);
                startActivity(intent);
            }
        });

        //Pinterest Activity
        ImageView img_pinterest = view.findViewById(R.id.img_pinterest);

        img_pinterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PinterestActivity.class);
                startActivity(intent);
            }
        });

        //Snapchat Activity
        ImageView img_snapchat = view.findViewById(R.id.img_snapchat);

        img_snapchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SnapchatActivity.class);
                startActivity(intent);
            }
        });

        //X Activity
        ImageView img_x = view.findViewById(R.id.img_x);

        img_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), XActivity.class);
                startActivity(intent);
            }
        });


//        // Hide the action bar
//        if (getActivity() != null) {
//            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
//        }

        return view;
    }

    //for custom Action bar name
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            // Set the title of the action bar
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("GENERATE");
        }
    }


//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Show the action bar
//        if (getActivity() != null) {
//            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).show();
//        }
//    }

}