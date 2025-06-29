package com.janhvi.qrshare.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.janhvi.qrshare.R;
import com.janhvi.qrshare.adapter.HistoryAdapter;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.DialogUtils;
import com.janhvi.qrshare.utility.Helper;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = HistoryFragment.class.getSimpleName();
    private View view;
    private Context context;
    private FrameLayout flHistoryFragment;
    private AppCompatAutoCompleteTextView acQRCodeType;
    private RecyclerView rvQRCode;
    private TextView tvNoData;
    private ExtendedFloatingActionButton extendedFbDeleteAllHistory;

    private HistoryAdapter adapter;
    private QRCode qrCodeEntity;
    private List<QRCode> qrCodeList;
    private DbHelper dbHelper;

    public HistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        initUI();
        initObj();
        initListener();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            setDataVisibility(false);
            qrCodeList.clear();
        }
        if (context != null) loadData();
    }

    private void setDataVisibility(boolean isDataAvailable) {
        if (isDataAvailable) {
            rvQRCode.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            rvQRCode.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void initUI() {
        flHistoryFragment = view.findViewById(R.id.flHistoryFragment);
        acQRCodeType = view.findViewById(R.id.acQRCodeType);
        rvQRCode = view.findViewById(R.id.rvQRCode);
        tvNoData = view.findViewById(R.id.tvNoData);
        extendedFbDeleteAllHistory = view.findViewById(R.id.extendedFbDeleteAllHistory);
    }

    private void initObj() {
        context = getContext();
        qrCodeEntity = new QRCode();
        dbHelper = new DbHelper(context);
    }

    private void initListener() {
        extendedFbDeleteAllHistory.setOnClickListener(this);
    }

    private void loadData() {
        try {
            if (qrCodeList == null) {
                qrCodeList = new ArrayList<>();
            }

            qrCodeList = dbHelper.getAllQRCode(); // Load from local DB

            if (!qrCodeList.isEmpty()) {
                setUpRecyclerView();
            } else {
                setDataVisibility(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in HistoryFragment", e);
            Helper.makeSnackBar(view, Constants.SOMETHING_WENT_WRONG);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {
        try {
            if (adapter != null) {
                adapter.updateQRCodeList(qrCodeList);
            } else {
                adapter = new HistoryAdapter(context, qrCodeList);
                rvQRCode.setAdapter(adapter);
                rvQRCode.setLayoutManager(Helper.getVerticalManager(context));
                adapter.notifyDataSetChanged();
            }
            setDataVisibility(true);
        } catch (Exception e) {
            Log.e(TAG, "Error in MyReportFragment", e);
            Helper.makeSnackBar(flHistoryFragment, Constants.SOMETHING_WENT_WRONG);
            setDataVisibility(false);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.extendedFbDeleteAllHistory) {
            onClickExtendedFbDeleteAllHistory();
        }
    }

    private void onClickExtendedFbDeleteAllHistory() {
        AlertDialog dialog = DialogUtils.confirmationDialog(
                context,
                "clear All QRCode History ?",
                (dialogInterface, i) -> {
                    dbHelper.deleteAllQRCodes();
                }
        );
        dialog.show();
    }

}