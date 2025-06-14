package com.janhvi.qrshare.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    public static final String TAG = LocationActivity.class.getSimpleName();
    private RelativeLayout rlLocationActivity;
    private TextInputEditText etLatitude, etLongitude;
    private AppCompatButton btnSubmit;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;
    private GoogleMap mMap;
    private SearchView searchView;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initToolbar();
        initUI();
        initObj();
        initListener();

        // Map initialization
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Location");
    }

    private void initUI() {
        rlLocationActivity = findViewById(R.id.rlLocationActivity);
        searchView = findViewById(R.id.searchView);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
        entity = new QRCode();
        dbHelper = new DbHelper(context);
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void initListener() {
        btnSubmit.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng india = new LatLng(20.5937, 78.9629);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 5));

        // Long press listener to add marker
        mMap.setOnMapLongClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            etLatitude.setText(String.valueOf(latLng.latitude));
            etLongitude.setText(String.valueOf(latLng.longitude));
        });
    }

    private void searchLocation(String locationName) {
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                etLatitude.setText(String.valueOf(latLng.latitude));
                etLongitude.setText(String.valueOf(latLng.longitude));
            } else {
                Helper.makeSnackBar(rlLocationActivity, "Location not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit) {
            try {
                onClickBtnSubmit();
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onClickBtnSubmit() throws WriterException {
        View[] view = {etLatitude, etLongitude};
        if (Helper.isEmptyFieldValidation(view)) {

            String latStr = Helper.getStringFromInput(etLatitude);
            String lngStr = Helper.getStringFromInput(etLongitude);

            double latitude = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(lngStr);

            // Move marker on map
            if (mMap != null) {
                LatLng location = new LatLng(latitude, longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(location).title("Selected Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }

            String content = "Location:\nLatitude: " + latStr + "\nLongitude: " + lngStr;
            Bitmap bitmap = Helper.textToImageEncode(context, content);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap); // convert Bitmap to byte[]
                QRCode qrCode = new QRCode();
                qrCode.setContent(content);
                qrCode.setType("Location");
                qrCode.setDate(Helper.getCurrentDate());
                qrCode.setTime(Helper.getCurrentTime());
                qrCode.setImage(imageBytes);
                qrCode.setIsFavorite(0);

                long result = dbHelper.addOrUpdateQRCode(qrCode);
                qrCode.setQid(result);
                Log.d(TAG, "qid: " + qrCode.getQid());
                if (result != -1) {
                    // Helper.makeSnackBar(rlTextActivity, "QR Code saved");
                    Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);
                } else {
                    Helper.makeSnackBar(rlLocationActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlLocationActivity, "Error generating QR code");
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}