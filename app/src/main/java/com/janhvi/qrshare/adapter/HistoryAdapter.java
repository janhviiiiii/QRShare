package com.janhvi.qrshare.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.janhvi.qrshare.R;
import com.janhvi.qrshare.activity.QRCodeActivity;
import com.janhvi.qrshare.database.DbHelper;
import com.janhvi.qrshare.fragment.HistoryFragment;
import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.utility.Constants;
import com.janhvi.qrshare.utility.DialogUtils;
import com.janhvi.qrshare.utility.Helper;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    public static final String TAG = HistoryAdapter.class.getSimpleName();
    private final Context context;
    private List<QRCode> qrCodeList;
    private DbHelper dbHelper;

    public HistoryAdapter(Context context, List<QRCode> qrCodeList) {
        this.context = context;
        this.qrCodeList = qrCodeList;
        this.dbHelper = new DbHelper(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateQRCodeList(List<QRCode> qrCodeList) {
        try {
            if (qrCodeList != null) {
                this.qrCodeList = qrCodeList;
                notifyDataSetChanged();
            }
        } catch (Exception exception) {
            Log.e(TAG, "Error in History Adapter", exception);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View detailItem = inflater.inflate(R.layout.list_view_history, parent, false);
        return new ViewHolder(detailItem);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (!qrCodeList.isEmpty()) {
                QRCode qrCode = qrCodeList.get(position);
                holder.tvQRType.setText(qrCode.getType());
                holder.tvQRContent.setText(qrCode.getContent());
                holder.tvQRDateTime.setText(qrCode.getDate() + " • " + qrCode.getTime());

                byte[] imageBytes = qrCode.getImage();
                if (imageBytes != null && imageBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmap != null) {
                        holder.ivQRCode.setImageBitmap(bitmap);
                    } else {
                        Log.e(TAG, "Failed to decode bitmap, using fallback image.");

                        holder.ivQRCode.setImageResource(R.drawable.ic_qrcode);
                    }
                } else {
                    Log.e(TAG, "Failed to decode bitmap, using fallback image.");

                    holder.ivQRCode.setImageResource(R.drawable.ic_qrcode);
                }

                holder.ivDelete.setOnClickListener(v -> {
                    String confirmationText = "remove QRCode from history";

                    AlertDialog dialog = DialogUtils.confirmationDialog(
                            context,
                            confirmationText,
                            (dialogInterface, i) -> {
//                                dbHelper.deleteQRCodeByQid((int) qrCode.getQid());

                                if (dbHelper.deleteQRCodeByQid((int) qrCode.getQid())) {
                                    qrCodeList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, qrCodeList.size()); // Optional
                                }
                            }
                    );
                    dialog.show();
                });

                holder.itemView.setOnClickListener(v -> {
//                    Helper.goToAndFinish(context, QRCodeActivity.class, Constants.QRCODE, qrCode);

                    Intent intent = new Intent(context, QRCodeActivity.class);
                    intent.putExtra(Constants.QRCODE, qrCode);
                    intent.putExtra(String.valueOf(Constants.IS_HISTORY_VIEW), true); // <- new flag
                    context.startActivity(intent);
                    ((Activity) context).finish();

                });

            }
        } catch (Exception e) {
            Log.e(TAG, "Error in History Adapter", e);
        }
    }


    @Override
    public int getItemCount() {
        return qrCodeList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQRType, tvQRContent, tvQRDateTime;
        private final ImageView ivQRCode, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQRType = itemView.findViewById(R.id.tvQRType);
            tvQRContent = itemView.findViewById(R.id.tvQRContent);
            tvQRDateTime = itemView.findViewById(R.id.tvQRDateTime);
            ivQRCode = itemView.findViewById(R.id.ivQRCode);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
