package com.example.qrshare;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class History_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<QRCodeInfo> qrCodeInfoList;
    private DatabaseHelper databaseHelper;

    private SearchView searchView;
    private List<QRCodeInfo> dataList;

    //for del btn
    private FloatingActionButton btn_delete;
    private TextView emptyView;
    private Context context;


    //Flag to keep track of whether history has been cleared
    private boolean isHistoryCleared = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_, container, false);

        context = getContext();


        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        btn_delete = view.findViewById(R.id.btn_delete);
        emptyView = view.findViewById(R.id.emptyView);


        // Set click listener for delete button
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());


        //load QR code history
        loadQRCodeHistory();

        return view;
    }


    // Method to perform search
    private void searchList(String text) {
        //if(historyAdapter != null) {
        historyAdapter.filter(text);
        //    }
    }


    //for custom Action bar name
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            // Set the title of the action bar
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("HISTORY");
        }


        //Load QR Code history whenever the fragment is resumed
        loadQRCodeHistory();
    }


    //for delete btn
    //method to show delete confirmation dialog box
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to clear the history?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteHistory();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    //Method to load QR Code history from the database if history has not been closed
    private void loadQRCodeHistory() {
        if (!isHistoryCleared) {
            qrCodeInfoList = databaseHelper.getAllQRCodeHistory();
        } else {
            qrCodeInfoList.clear(); //clear the list if hist has been cleared
        }

        //Update adapter and visibility of views
        historyAdapter = new HistoryAdapter(context, qrCodeInfoList);
        recyclerView.setAdapter(historyAdapter);
        updateViewVisibility();
    }


    //method to delete history
    private void deleteHistory() {
        //to clear history frag but keeping fav frag as it is
//        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete("qrcodes", "favorite = 0", null); //favorite = 0, to keep fav frag untouched
        db.close();



        //Update flag and clear list
        isHistoryCleared = true;
        loadQRCodeHistory();
    }

        //Update visibility of emptyView and recyclerView
    private void updateViewVisibility(){
        if(qrCodeInfoList.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else{
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }


}