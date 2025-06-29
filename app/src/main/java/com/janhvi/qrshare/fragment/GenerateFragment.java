package com.janhvi.qrshare.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.janhvi.qrshare.R;
import com.janhvi.qrshare.activity.CompanyActivity;
import com.janhvi.qrshare.activity.ContactActivity;
import com.janhvi.qrshare.activity.CopyActivity;
import com.janhvi.qrshare.activity.EmailActivity;
import com.janhvi.qrshare.activity.EventActivity;
import com.janhvi.qrshare.activity.MoreSocialActivity;
import com.janhvi.qrshare.activity.SocialActivity;
import com.janhvi.qrshare.activity.LocationActivity;
import com.janhvi.qrshare.activity.PhoneActivity;
import com.janhvi.qrshare.activity.ProfileActivity;
import com.janhvi.qrshare.activity.SmsActivity;
import com.janhvi.qrshare.activity.TextActivity;
import com.janhvi.qrshare.activity.WebsiteActivity;
import com.janhvi.qrshare.activity.WifiActivity;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;


public class GenerateFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = GenerateFragment.class.getSimpleName();
    private View view;
    private Context context;
    private CardView cvText, cvWebsite, cvSms, cvCompany, cvEvent, cvPhone, cvContact, cvProfile, cvEmail, cvCopy, cvLocation, cvWifi;
    private CardView cvInstagram, cvX, cvYoutube, cvPinterest, cvSnapchat, cvSpotify, cvFacebook, cvWhatsapp, cvLinkedin;

    public GenerateFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
            Helper.goTo(context, TextActivity.class);
        } else if (id == R.id.cvWebsite) {
            Helper.goTo(context, WebsiteActivity.class);
        } else if (id == R.id.cvContact) {
            Helper.goTo(context, ContactActivity.class);
        } else if (id == R.id.cvPhone) {
            Helper.goTo(context, PhoneActivity.class);
        } else if (id == R.id.cvEmail) {
            Helper.goTo(context, EmailActivity.class);
        } else if (id == R.id.cvProfile) {
            Helper.goTo(context, ProfileActivity.class);
        } else if (id == R.id.cvSms) {
            Helper.goTo(context, SmsActivity.class);
        } else if (id == R.id.cvEvent) {
            Helper.goTo(context, EventActivity.class);
        } else if (id == R.id.cvCompany) {
            Helper.goTo(context, CompanyActivity.class);
        } else if (id == R.id.cvCopy) {
            Helper.goTo(context, CopyActivity.class);
        } else if (id == R.id.cvLocation) {
            Helper.goTo(context, LocationActivity.class);
        } else if (id == R.id.cvWifi) {
            Helper.goTo(context, WifiActivity.class);
        } else if (id == R.id.cvInstagram) {
            Helper.goToWithSocialType(context, SocialActivity.class, Constants.SOCIAL_TYPE, Constants.INSTAGRAM);
        } else if (id == R.id.cvLinkedin) {
            Helper.goToWithSocialType(context, MoreSocialActivity.class, Constants.SOCIAL_TYPE, Constants.LINKEDIN);
        } else if (id == R.id.cvYoutube) {
            Helper.goToWithSocialType(context, SocialActivity.class, Constants.SOCIAL_TYPE, Constants.YOUTUBE);
        } else if (id == R.id.cvWhatsapp) {
            Helper.goToWithSocialType(context, MoreSocialActivity.class, Constants.SOCIAL_TYPE, Constants.WHATSAPP);
        } else if (id == R.id.cvFacebook) {
            Helper.goToWithSocialType(context, SocialActivity.class, Constants.SOCIAL_TYPE, Constants.FACEBOOK);
        } else if (id == R.id.cvSpotify) {
            Helper.goToWithSocialType(context, MoreSocialActivity.class, Constants.SOCIAL_TYPE, Constants.SPOTIFY);
        } else if (id == R.id.cvPinterest) {
            Helper.goToWithSocialType(context, MoreSocialActivity.class, Constants.SOCIAL_TYPE, Constants.PINTEREST);
        } else if (id == R.id.cvSnapchat) {
            Helper.goToWithSocialType(context, MoreSocialActivity.class, Constants.SOCIAL_TYPE, Constants.SNAPCHAT);
        } else if (id == R.id.cvX) {
            Helper.goToWithSocialType(context, MoreSocialActivity.class, Constants.SOCIAL_TYPE, Constants.X);
        }
    }
}