package com.example.qrshare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private ArrayList<QRCodeInfo> favoriteQRCodeList;

    public FavoriteAdapter(Context context, ArrayList<QRCodeInfo> favoriteQRCodeList) {
        this.context = context;
        this.favoriteQRCodeList = favoriteQRCodeList;

    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_qr_code, parent, false);
        return new FavoriteViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, @SuppressLint("RecyclerView") int position) {

        QRCodeInfo qrCodeInfo = favoriteQRCodeList.get(position);
        holder.bind(qrCodeInfo);

        //new
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QRCodeDetailActivity.class);
                intent.putExtra("image", qrCodeInfo.getImageByteArray());
                intent.putExtra("content", qrCodeInfo.getContent());
                intent.putExtra("type", qrCodeInfo.getType());
                intent.putExtra("timestamp", qrCodeInfo.getTimestamp());

//                intent.putExtra("qrCodeInfo", qrCodeInfo);

                context.startActivity(intent);
            }
        });

        holder.btn_remove_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //remove qr code from favorite
                removeFavorite(position);
            }
        });
    }


    public void removeFavorite(int position) {
        //remove QR Code from the list
        QRCodeInfo qrCodeInfo = favoriteQRCodeList.get(position);
        favoriteQRCodeList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favoriteQRCodeList.size());

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        boolean isSuccess = databaseHelper.removeFromFavorites(qrCodeInfo.getContent());

        if(isSuccess){
            //show a toast msg if removed successfully
            Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
        }
        else{
            //show toast msg failed to remove
            Toast.makeText(context, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return favoriteQRCodeList.size();
    }


    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        public ImageButton btn_remove_fav;
        private TextView contentTextView;
        private TextView typeTextView;
        private TextView timestampTextView;
        private ImageView imageViewQRCode;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.textContent);
            typeTextView = itemView.findViewById(R.id.textType);
            timestampTextView = itemView.findViewById(R.id.textTimestamp);
            imageViewQRCode = itemView.findViewById(R.id.imageViewQRCode);
            btn_remove_fav = itemView.findViewById(R.id.btn_remove_fav);
        }

        public void bind(QRCodeInfo qrCodeInfo) {
            contentTextView.setText("Content: " + qrCodeInfo.getContent());
            typeTextView.setText("Type: " + qrCodeInfo.getType());
            timestampTextView.setText("Timestamp: " + qrCodeInfo.getTimestamp());

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
