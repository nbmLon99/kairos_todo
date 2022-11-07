package com.nbmlon.dialogmodule.display;

import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImagePickerSlide_adapter extends RecyclerView.Adapter<ImagePickerSlide_adapter.ViewHolder> {
    private final ArrayList<Uri> mUris;

    public ImagePickerSlide_adapter(ArrayList<Uri> uris) {
        mUris = uris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView iv = new ImageView(parent.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        iv.setLayoutParams(params);
        return new ViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageURI(mUris.get(position));
    }

    @Override
    public int getItemCount() {
        return mUris.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull ImageView iv) {
            super(iv);
            this.imageView = iv;
        }
    }
}
