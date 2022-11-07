package com.nbmlon.dialogmodule.display;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.dialogmodule.R;

import java.util.ArrayList;

public class ImagePickerSlide_bar extends RecyclerView.Adapter<ImagePickerSlide_bar.ViewHolder> {

    private ArrayList<Boolean> mMarkers = new ArrayList<>();
    private int mIndex =0;

    public ImagePickerSlide_bar(int size) {
        if (size > 0){
            for (int i=0; i<size; i++)
                mMarkers.add(false);
            mMarkers.set(mIndex,true);
        }
    }

    public void ImageSelected(int index){
        mMarkers.set(mIndex, false);
        notifyItemChanged(mIndex);
        mMarkers.set(index, true);
        notifyItemChanged(index);
        mIndex = index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(15,15);
        params.setMargins(3,3,3,3);
        ImageView iv = new ImageView(parent.getContext());
        iv.setLayoutParams(params);
        return new ViewHolder(iv);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(mMarkers.get(position)? R.drawable.icon_true : R.drawable.icon_false);
    }

    @Override
    public int getItemCount() {
        return mMarkers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }
    }
}
