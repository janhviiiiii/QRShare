package com.janhvi.qrshare.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = EventActivity.class.getSimpleName();
    private RelativeLayout rlEventActivity;
    private TextInputEditText etName, etLocation, etDescription, etStartDateTime, etEndDateTime;
    private AppCompatButton btnSubmit;
    private Context context;
    private QRCode entity;
    private DbHelper dbHelper;
    private Locale Locale;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initToolbar();
        initUI();
        initObj();
        initListener();
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event");
    }

    private void initUI() {
        rlEventActivity = findViewById(R.id.rlEventActivity);
        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etStartDateTime = findViewById(R.id.etStartDateTime);
        etEndDateTime = findViewById(R.id.etEndDateTime);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initObj() {
        context = this;
        entity = new QRCode();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        etStartDateTime.setOnClickListener(this);
        etEndDateTime.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
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
        } else if (id == R.id.etStartDateTime) {
            onClickOfStartDateTime();
        } else if (id == R.id.etEndDateTime) {
            onClickOfEndDateTime();
        }
    }

    private void onClickOfStartDateTime() {
        final Calendar calendar = Calendar.getInstance();

        // First show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // After date is picked, show TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Format and set to EditText
                        String formattedDateTime = dateTimeFormat.format(calendar.getTime());
                        etStartDateTime.setText(formattedDateTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);  // true for 24-hour format
                timePickerDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void onClickOfEndDateTime() {
        final Calendar calendar = Calendar.getInstance();

        // First show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // After date is picked, show TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Format and set to EditText
                        String formattedDateTime = dateTimeFormat.format(calendar.getTime());
                        etEndDateTime.setText(formattedDateTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);  // true for 24-hour format
                timePickerDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void onClickBtnSubmit() throws WriterException {
        View[] view = {etName, etLocation, etStartDateTime, etEndDateTime, etDescription};
        if (Helper.isEmptyFieldValidation(view)) {
            String content = "Event:\nName: " + Helper.getStringFromInput(etName) +
                    "\nLocation: " + Helper.getStringFromInput(etLocation) +
                    "\nStart: " + Helper.getStringFromInput(etStartDateTime) +
                    "\nEnd: " + Helper.getStringFromInput(etEndDateTime) +
                    "\nDescription: " + Helper.getStringFromInput(etDescription);
            Bitmap bitmap = Helper.textToImageEncode(context, content);

            if (bitmap != null) {
                byte[] imageBytes = Helper.bitmapToByteArray(bitmap); // convert Bitmap to byte[]
                QRCode qrCode = new QRCode();
                qrCode.setContent(content);
                qrCode.setType("Event");
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
                    Helper.makeSnackBar(rlEventActivity, "Failed to generate QR Code");
                }
            } else {
                Helper.makeSnackBar(rlEventActivity, "Error generating QR code");
            }
        }
    }
}