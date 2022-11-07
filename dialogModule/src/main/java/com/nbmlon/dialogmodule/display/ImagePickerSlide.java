package com.nbmlon.dialogmodule.display;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.nbmlon.dialogmodule.R;

import java.util.ArrayList;

public class ImagePickerSlide extends RelativeLayout {

    private final Context mContext;
    private RecyclerView mImageRv;
    private RecyclerView mImageBarRv;

    private int lastPosition = -1;

    private com.nbmlon.dialogmodule.display.ImagePickerSlide_bar mBarAdapter;


    public ImagePickerSlide(@NonNull Context context) {
        this(context,null);
    }

    public ImagePickerSlide(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    public void init(){
        RelativeLayout imageRoot = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.imagepicker , this, false);
        mImageRv =  imageRoot.findViewById(R.id.picker_imageRV);
        mImageBarRv = imageRoot.findViewById(R.id.picker_barRV);

        mImageRv.setLayoutManager( new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mImageBarRv.setLayoutManager( new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mImageRv.setAdapter(EmptyAdapter());
        mImageBarRv.setAdapter(EmptyAdapter());

        addView(imageRoot);

        post(() -> {
            LinearSnapHelper snapHelper=new LinearSnapHelper();
            snapHelper.attachToRecyclerView(mImageRv);

            RecyclerView.OnScrollListener sl = new ScrollListener(snapHelper, mImageRv.getLayoutManager());
            mImageRv.removeOnScrollListener(sl);
            mImageRv.addOnScrollListener(sl);
        });
    }



    public void setImageUris(ArrayList<Uri> uris){
        if (uris.size()<=0){
            this.setVisibility(View.GONE);
        }
        else{
            ImagePickerSlide_adapter mImageAdapter = new ImagePickerSlide_adapter(uris);
            mBarAdapter = new ImagePickerSlide_bar(uris.size());

            mImageRv.setAdapter(mImageAdapter);
            mImageBarRv.setAdapter(mBarAdapter);
        }
    }




    private class ScrollListener extends RecyclerView.OnScrollListener {

        private final SnapHelper mSnaphelper;
        private RecyclerView.LayoutManager mLayoutManager;

        public ScrollListener(LinearSnapHelper snapHelper, RecyclerView.LayoutManager lm) {
            this.mSnaphelper = snapHelper;
            this.mLayoutManager = lm;
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int position = mLayoutManager.getPosition(mSnaphelper.findSnapView(mLayoutManager));
                if (position != -1 && position != lastPosition) {
                    mBarAdapter.ImageSelected(position);
                    lastPosition = position;
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    private RecyclerView.Adapter EmptyAdapter(){
        return new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
    }
}
