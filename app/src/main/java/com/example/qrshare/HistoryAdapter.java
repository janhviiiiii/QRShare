package com.example.qrshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<QRCodeInfo> qrCodeInfoList;

    private List<QRCodeInfo> originalList; // Store the original list for filtering

    public HistoryAdapter(Context context, ArrayList<QRCodeInfo> qrCodeInfoList) {
        this.context = context;
        this.qrCodeInfoList = qrCodeInfoList;


        this.originalList = new ArrayList<>(qrCodeInfoList); // Copy original list

    }


    // Method to filter the data
    public void filter(String searchText) {
        qrCodeInfoList.clear(); // Clear current list
        if (searchText.isEmpty()) {
            qrCodeInfoList.addAll(originalList); // If search text is empty, show original list
        } else {
            searchText = searchText.toLowerCase();
            for (QRCodeInfo item : originalList) {
                if (item.getType().toLowerCase().contains(searchText)) {
                    qrCodeInfoList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_code, parent, false);
        return new ViewHolder(view);
        }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QRCodeInfo qrCodeInfo = qrCodeInfoList.get(position);
        holder.bind(qrCodeInfo);


        //for recycler view click
          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QRCodeDetailActivity.class);
                intent.putExtra("image", qrCodeInfo.getImageByteArray());
                intent.putExtra("content", qrCodeInfo.getContent());
                intent.putExtra("type", qrCodeInfo.getType());
                intent.putExtra("timestamp", qrCodeInfo.getTimestamp());

                context.startActivity(intent);

            }
        });

        }

    @Override
    public int getItemCount() {
        return qrCodeInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView contentTextView;
        private TextView typeTextView;
        private TextView timestampTextView;
        private ImageView imageViewQRCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.textContent);
            typeTextView = itemView.findViewById(R.id.textType);
            timestampTextView = itemView.findViewById(R.id.textTimestamp);
            imageViewQRCode = itemView.findViewById(R.id.imageViewQRCode);
        }

        public void bind(QRCodeInfo qrCodeInfo) {
            contentTextView.setText("Content: "+qrCodeInfo.getContent());
            typeTextView.setText("Type: "+qrCodeInfo.getType());
            timestampTextView.setText("Timestamp: "+qrCodeInfo.getTimestamp());

            // Load the image from the byte array stored in QRCodeInfo
            byte[] imageByteArray = qrCodeInfo.getImageByteArray();
            if (imageByteArray != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                imageViewQRCode.setImageBitmap(bitmap);
            } else {
                // Handle case where imageByteArray is null or empty
                imageViewQRCode.setImageResource(R.drawable.placeholder_image);

            }

        }
    }

}


