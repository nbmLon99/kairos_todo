package com.nbmlon.customcalender_myfirstapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdSize;
import com.nbmlon.dialogmodule.gallery.DeleteListener;
import com.nbmlon.dialogmodule.gallery.Dialog_Gallery;
import com.nbmlon.managemodule.FileManager;

import java.util.ArrayList;

public class ResultGalleryActivity extends AppCompatActivity implements DeleteListener {


    private static final int rowCount = 4;
    private static final int colCount = 3;

    private TextView tv;
    private GridLayout gl;
    private Integer mDstYear;
    private Integer mPosition;


    private static int gl_width;
    private static int gl_height;


    private ArrayList<Uri> mUris;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_gallery);

        ((Runnable) () -> {CustomCalender.getAds(ResultGalleryActivity.this, findViewById(R.id.gallery_adView));}).run();

        tv = findViewById(R.id.resglr_tv);
        gl = findViewById(R.id.resglr_gl);
        setFrame();
        gl.post(() -> {
            gl_width = gl.getWidth();
            gl_height = gl.getHeight() - AdSize.BANNER.getHeightInPixels(ResultGalleryActivity.this);
            setGallery();

        });
    }
    private void setFrame(){
        ImageView icon_back = findViewById(R.id.resglr_btn_back);
        ImageView icon_next = findViewById(R.id.resglr_btn_next);

        mPosition = CustomCalender.RESULT_MONTHS.size()-1;
        mDstYear = CustomCalender.RESULT_MONTHS.get(mPosition);
        if (CustomCalender.RESULT_MONTHS.size() > 1){
            ((Runnable) () -> {
                icon_back.setVisibility(View.VISIBLE);
                icon_back.setOnClickListener(v -> {
                    mPosition--;
                    mDstYear = CustomCalender.RESULT_MONTHS.get(mPosition);
                    tv.setText("" + mDstYear);
                    ((Runnable) () -> setGallery()).run();
                    icon_back.setVisibility(mPosition <= 0 ? View.GONE : View.VISIBLE);
                    icon_next.setVisibility(mPosition >= CustomCalender.RESULT_MONTHS.size() - 1 ?
                            View.GONE : View.VISIBLE);
                });

                icon_next.setOnClickListener(v -> {
                    mPosition++;
                    mDstYear = CustomCalender.RESULT_MONTHS.get(mPosition);
                    tv.setText(Integer.toString(mDstYear));

                    ((Runnable) () -> setGallery()).run();
                    icon_back.setVisibility(mPosition <= 0 ? View.GONE : View.VISIBLE);
                    icon_next.setVisibility(mPosition >= CustomCalender.RESULT_MONTHS.size() - 1 ?
                            View.GONE : View.VISIBLE);
                });
            }).run();
        }
        else{
            icon_back.setVisibility(View.GONE);
            icon_next.setVisibility(View.GONE);

        }

        tv.setText(Integer.toString(mDstYear));

    }


    private void setGallery(){
        gl.removeAllViews();
        Thread getUris = new Thread(() -> {
            FileManager.Gallery resFileManager = new FileManager.Gallery(ResultGalleryActivity.this);
            mUris = resFileManager.getResultGalleryBitmaps(mDstYear);
        });
        getUris.start();

        for(int i =0; i<12 ; i++){
            LinearLayout ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gl_width / colCount, gl_height / rowCount);
            ll.setLayoutParams(params);
            ll.setOrientation(LinearLayout.VERTICAL);

            TextView tv = new TextView(this);
            ImageView iv = new ImageView(this);
            iv.setAdjustViewBounds(true);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setPadding(15,15,15,15);

            tv.setText((i+1) + " 월");
            tv.setTextSize(10);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextColor(getBaseContext().getColor(com.nbmlon.managemodule.R.color.signature_blue));


            Uri uri;
            try {
                getUris.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if((uri = mUris.get(i)) != null){
                iv.setImageURI(uri);
                final int month = i + 1;
                ll.setOnClickListener(v -> {
                    Dialog_Gallery gallery = new Dialog_Gallery(ResultGalleryActivity.this, ResultGalleryActivity.this, uri, mDstYear, month);
                    gallery.show();
                });

            }

            ll.addView(tv);
            ll.addView(iv);


            gl.addView(ll);
        }

    }


    @Override
    public void calenderDeleted(int dstYear, int dstMonth, int lefted_count) {
        if(lefted_count > 0)
            setGallery();
        else{
            CustomCalender.RESULT_MONTHS.remove((Integer) dstYear);
            if (CustomCalender.RESULT_MONTHS.size() <= 0) {
                this.finish();
            }
            else{
                setFrame();
                setGallery();
            }
        }
    }
}
