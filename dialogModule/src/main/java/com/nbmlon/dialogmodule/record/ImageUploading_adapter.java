package com.nbmlon.dialogmodule.record;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.dialogmodule.R;

import java.util.ArrayList;

public class ImageUploading_adapter extends RecyclerView.Adapter<ImageUploading_adapter.ViewHolder> {

    private ArrayList<Uri> mItems = new ArrayList<>();

    public boolean isIMAGE_UPDATED() {
        return IMAGE_UPDATED;
    }
    private boolean IMAGE_UPDATED = false;
    int mHeight;

    public void AddUris(ArrayList<Uri> uris){
        int index = getItemCount();
        mItems.addAll(uris);
        notifyItemInserted(index);
        IMAGE_UPDATED = true;
    }

    public void setUpdateListener(){
        IMAGE_UPDATED = false;
    }


    public ImageUploading_adapter(int parentHeight) {
        mHeight = parentHeight;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.image_uploaded, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri item = mItems.get(position);
        holder.uploaded_image.setImageURI(item);
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public ArrayList<Uri> getItems() { return mItems; }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView uploaded_image;
        ImageView btn_delete;
        FrameLayout imageFrame;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uploaded_image = (ImageView) itemView.findViewById(R.id.image_uploaded);
            btn_delete = (ImageView) itemView.findViewById(R.id.btn_delete);
            imageFrame = (FrameLayout) itemView.findViewById(R.id.image_uploaded_Frame);

            FrameLayout.LayoutParams params = new
                    FrameLayout.LayoutParams(mHeight, mHeight);
            imageFrame.setLayoutParams(params);

            btn_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int curPos = getAdapterPosition();
            mItems.remove(curPos);
            notifyItemRemoved(curPos);
            IMAGE_UPDATED = true;
        }
    }


}
