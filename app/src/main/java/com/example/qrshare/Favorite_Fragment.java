package com.example.qrshare;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;


public class Favorite_Fragment extends Fragment {

    private RecyclerView favoriteRecyclerView;
    private FavoriteAdapter favoriteAdapter;
    private ArrayList<QRCodeInfo> favoriteQRCodeList;
    private DatabaseHelper databaseHelper;

    private TextView emptyView;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_, container, false);

        context = getContext();


        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Initialize RecyclerView
        favoriteRecyclerView = view.findViewById(R.id.favoriteRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoriteQRCodeList = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(requireContext(), favoriteQRCodeList);
        favoriteRecyclerView.setAdapter(favoriteAdapter);

        // Load favorite QR codes from the database
        loadFavoriteQRCodeList();

        // Retrieve QR code favorite from the database
        favoriteQRCodeList = databaseHelper.getFavoriteQRCodeList();


        //for del btn
        if (favoriteQRCodeList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            favoriteRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            favoriteRecyclerView.setVisibility(View.VISIBLE);

            favoriteAdapter = new FavoriteAdapter(context, favoriteQRCodeList);
            favoriteRecyclerView.setAdapter(favoriteAdapter);
        }
        return view;
    }


    private void loadFavoriteQRCodeList() {
        // Retrieve all favorite QR codes from the database
        favoriteQRCodeList.clear();
        favoriteQRCodeList.addAll(databaseHelper.getFavoriteQRCodeList());
        favoriteAdapter.notifyDataSetChanged();

        emptyView.setVisibility(View.VISIBLE);

    }


    //for custom Action bar name
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            // Set the title of the action bar
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("FAVORITE");
        }
    }
}