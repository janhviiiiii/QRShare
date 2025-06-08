package com.janhvi.qrshare.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.janhvi.qrshare.R;


public class GenerateFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = GenerateFragment.class.getSimpleName();
    private View view;
    private Context context;
    private CardView cvText, cvWebsite, cvSms, cvCompany, cvEvent, cvPhone, cvContact, cvProfile, cvEmail, cvCopy, cvLocation, cvWifi;
    private CardView cvInstagram, cvX, cvYoutube, cvPinterest, cvSnapchat, cvSpotify, cvFacebook, cvWhatsapp, cvLinkedin;

    public GenerateFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        context = view.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_generate, container, false);

        initUI();
        initObj();
        initListener();

        return view;
    }

    private void initUI() {
        cvText = view.findViewById(R.id.cvText);
        cvWebsite = view.findViewById(R.id.cvWebsite);
        cvSms = view.findViewById(R.id.cvSms);
        cvCompany = view.findViewById(R.id.cvCompany);
        cvEvent = view.findViewById(R.id.cvEvent);
        cvPhone = view.findViewById(R.id.cvPhone);
        cvContact = view.findViewById(R.id.cvContact);
        cvProfile = view.findViewById(R.id.cvProfile);
        cvEmail = view.findViewById(R.id.cvEmail);
        cvCopy = view.findViewById(R.id.cvCopy);
        cvLocation = view.findViewById(R.id.cvLocation);
        cvWifi = view.findViewById(R.id.cvWifi);
        cvInstagram = view.findViewById(R.id.cvInstagram);
        cvX = view.findViewById(R.id.cvX);
        cvYoutube = view.findViewById(R.id.cvYoutube);
        cvPinterest = view.findViewById(R.id.cvPinterest);
        cvSnapchat = view.findViewById(R.id.cvSnapchat);
        cvSpotify = view.findViewById(R.id.cvSpotify);
        cvFacebook = view.findViewById(R.id.cvFacebook);
        cvWhatsapp = view.findViewById(R.id.cvWhatsapp);
        cvLinkedin = view.findViewById(R.id.cvLinkedin);
    }

    private void initListener() {
        cvText.setOnClickListener(this);
        cvWebsite.setOnClickListener(this);
        cvSms.setOnClickListener(this);
        cvCompany.setOnClickListener(this);
        cvEvent.setOnClickListener(this);
        cvPhone.setOnClickListener(this);
        cvContact.setOnClickListener(this);
        cvProfile.setOnClickListener(this);
        cvEmail.setOnClickListener(this);
        cvCopy.setOnClickListener(this);
        cvLocation.setOnClickListener(this);
        cvWifi.setOnClickListener(this);
        cvInstagram.setOnClickListener(this);
        cvX.setOnClickListener(this);
        cvYoutube.setOnClickListener(this);
        cvPinterest.setOnClickListener(this);
        cvSnapchat.setOnClickListener(this);
        cvSpotify.setOnClickListener(this);
        cvFacebook.setOnClickListener(this);
        cvWhatsapp.setOnClickListener(this);
        cvLinkedin.setOnClickListener(this);
    }

    private void initObj() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cvText) {

        } else if (id == R.id.cvText) {

        } else if (id == R.id.cvWebsite) {

        } else if (id == R.id.cvContact) {

        } else if (id == R.id.cvPhone) {

        } else if (id == R.id.cvEmail) {

        } else if (id == R.id.cvProfile) {

        } else if (id == R.id.cvSms) {

        } else if (id == R.id.cvEvent) {

        } else if (id == R.id.cvCompany) {

        } else if (id == R.id.cvCopy) {

        } else if (id == R.id.cvLocation) {

        } else if (id == R.id.cvWifi) {

        } else if (id == R.id.cvInstagram) {

        } else if (id == R.id.cvLinkedin) {

        } else if (id == R.id.cvYoutube) {

        } else if (id == R.id.cvWhatsapp) {

        } else if (id == R.id.cvFacebook) {

        } else if (id == R.id.cvSpotify) {

        } else if (id == R.id.cvPinterest) {

        } else if (id == R.id.cvSnapchat) {

        } else if (id == R.id.cvX) {

        }
    }
}