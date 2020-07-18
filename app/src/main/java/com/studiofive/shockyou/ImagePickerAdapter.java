package com.studiofive.shockyou;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>{
    List<ImageModel> items;
    Callback callback;

    public ImagePickerAdapter(List<ImageModel> items, Callback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ImageModel item = items.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.itemSelected(item);
            }
        });
        Uri imgUri;
        if (item.isAsset()){
            imgUri = ShockUtils.getDrawableUri(holder.itemView.getContext(), item.getImgFilename());
        }else {
            imgUri = Uri.fromFile(new File(item.getImgFilename()));
        }
        Glide.with(holder.itemView.getContext())
                .load(imgUri)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    interface Callback{
        void itemSelected(ImageModel item);
    }
}
