//not using
package com.example.qrshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VPAdapter extends RecyclerView.Adapter<VPAdapter.ViewHolder> {


    ArrayList<viewpageItem> viewpageItemArrayList;

    public VPAdapter(ArrayList<viewpageItem> viewpageItemArrayList) {
        this.viewpageItemArrayList = viewpageItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewpager_item1,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        viewpageItem viewPagerItem = viewpageItemArrayList.get(position);

        holder.imageView.setImageResource(viewPagerItem.imageID);
        holder.tcHeading.setText(viewPagerItem.heading);
        holder.tvDesc.setText(viewPagerItem.description);

    }

    @Override
    public int getItemCount() {
        return viewpageItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tcHeading, tvDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivimage);
            tcHeading = itemView.findViewById(R.id.tvHeading);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }
    }
}
