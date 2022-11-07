package com.nbmlon.dialogmodule.record;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageUploadingFrame extends RecyclerView{

    public ImageUploading_adapter mAdapter;

    public boolean IMAGE_UPDATED() {
        return mAdapter.isIMAGE_UPDATED();
    }

    public ImageUploadingFrame(Context context) {
        this(context, null);
    }

    public ImageUploadingFrame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutParams params = new
                LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        setLayoutParams(params);

        setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        setAdapter(new Adapter() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            }
            @Override
            public int getItemCount() {
                return 0;
            }
        });

        post(() -> {
            mAdapter = new ImageUploading_adapter(getHeight());
            setAdapter(mAdapter);
        });

    }

    public void AddAdapterItems(ArrayList<Uri> uris) {
        mAdapter.AddUris(uris);
    }

    public void SetImageUpdateListener(){
        mAdapter.setUpdateListener();
    }

    public ArrayList<Uri> getUris(){return mAdapter.getItems();}
}
