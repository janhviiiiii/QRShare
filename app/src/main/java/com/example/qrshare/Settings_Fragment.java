package com.example.qrshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;


public class Settings_Fragment extends Fragment {

    private TextView user_username, user_email, txt_username, txt_email, txt_aboutus;
    private Button btn_logout;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_, container, false);

        txt_username = view.findViewById(R.id.txt_username);
        txt_email = view.findViewById(R.id.txt_email);
        user_username = view.findViewById(R.id.user_username);
        user_email = view.findViewById(R.id.user_email);
        btn_logout = view.findViewById(R.id.btn_logout);
        txt_aboutus = view.findViewById(R.id.txt_aboutus);


        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(requireContext());

        // Load user profile data
        loadUserProfile();


        // Logout button click listener
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                //Clear the username and email TextViews
                user_username.setText("");
                user_email.setText("");

                //to clear the history and favorite
                clearQRCodeHistory();
                clearFavoriteQRCodeList();

                // Redirect to MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
//                getActivity().finish(); // Finish the current activity
                requireActivity().finish(); // Finish the current activity
            }
        });

        txt_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to AboutUsActivity
                Intent intent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadUserProfile() {
        // Fetch user data from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        User user = databaseHelper.getUserData();

        // Check if user data is fetched successfully
        if (user != null) {
            // Display username and email
            user_username.setText(user.getUsername());
            user_email.setText(user.getEmail());
        }
    }



    // Method to clear QR code history
    private void clearQRCodeHistory() {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.clearQRCodeHistory();
    }

    // Method to clear favorite QR code list
    private void clearFavoriteQRCodeList() {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.clearQRCodeHistory();
    }




    //for custom Action bar name
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            // Set the title of the action bar
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("MORE");
        }
    }
}