package com.nbmlon.mainmodule.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;

public class CropView {

    private Context mContext;

    private ImageView mCropIV;
    private Crop_MultiTouch mCropTouchListener;

    private int mSize;
    private boolean Resized = false;
    private float orgWidth, orgHeight;

    private cropListener cl;


    public CropView(cropListener cl) {
        this.cl = cl;
    }

    /**Crop View Inflate onto MainImage **/
    public void inflate(RelativeLayout parent, int size,  Uri uri, int leftMargin, int topMargin) {
        mContext = parent.getContext();
        mSize = size;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout crop_view = (RelativeLayout) inflater.inflate(com.nbmlon.mainmodule.R.layout.crop_layout, parent, false);

        RelativeLayout CropLayout = (RelativeLayout) crop_view.findViewById(com.nbmlon.mainmodule.R.id.crop_Frame);
        mCropIV = (ImageView) crop_view.findViewById(com.nbmlon.mainmodule.R.id.crop_iv);
        ImageView btn_crop = (ImageView) crop_view.findViewById(com.nbmlon.mainmodule.R.id.btn_crop);
        ImageView btn_resize = (ImageView) crop_view.findViewById(com.nbmlon.mainmodule.R.id.btn_resize);
        ImageView btn_cancel = (ImageView) crop_view.findViewById(com.nbmlon.mainmodule.R.id.btn_cancel_crop);

        setImageForCrop(uri);
        RelativeLayout.MarginLayoutParams params = new RelativeLayout.LayoutParams(mSize, mSize);
        params.leftMargin = leftMargin; params.topMargin = topMargin;
        CropLayout.setLayoutParams(params);



        crop_view.setOnTouchListener((v, event) -> true);



        mCropIV.post(new Runnable() {
            @Override
            public void run() {
                mCropTouchListener = new Crop_MultiTouch(mCropIV);
                mCropIV.setOnTouchListener(mCropTouchListener);
            }
        });

        ((Runnable) () -> {
            btn_crop.setOnClickListener(v -> {
                cl.cropDone(mCropIV);
                parent.removeView(crop_view);
            });

            btn_resize.setOnClickListener(v -> Resize());

            btn_cancel.setOnClickListener(v -> parent.removeView(crop_view));

        }).run();


        parent.addView(crop_view);


    }

    private void Resize(){
        Matrix tmpMatrix = new Matrix();
        if(Resized){
            Resized = false;
        }
        else{
            float ratioX = orgWidth > orgHeight ? orgHeight / orgWidth : 1;
            float ratioY = orgWidth > orgHeight ? 1 : orgWidth / orgHeight;
            tmpMatrix.setScale(ratioX, ratioY);
            Resized = true;
        }

        mCropIV.setImageMatrix(tmpMatrix);
        mCropTouchListener.Resized();
    }



    /** setting first size of image **/
    private void setImageForCrop(Uri uri){

        float width, height;

        try {
            Bitmap orgBitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                orgBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.getContentResolver(), uri));
            } else {
                orgBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            }

            orgWidth = orgBitmap.getWidth();
            orgHeight = orgBitmap.getHeight();


            if (orgHeight < orgWidth){
                height = mSize;
                width = height * (orgWidth / orgHeight);
            }
            else{
                width = mSize;
                height = width * (orgHeight / orgWidth);
            }

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap,(int)width,(int)height,true);
            mCropIV.setImageBitmap(scaledBitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
