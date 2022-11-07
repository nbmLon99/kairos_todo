package com.nbmlon.mainmodule.resultgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nbmlon.managemodule.FileManager;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Uri> mUris;

    public GalleryAdapter(Context context, int dstYear) {
        mContext = context;
        //todo year로부터 mBitmaps 가져오기
        FileManager.Gallery resFileManager = new FileManager.Gallery(context);
        mUris = resFileManager.getResultGalleryBitmaps(dstYear);
    }


    @Override
    public int getCount() {
        return mUris.size();
    }

    @Override
    public Object getItem(int position) {
        return mUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout ll = new LinearLayout(mContext);

        ll.setLayoutParams(new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(mContext);
        ImageView iv = new ImageView(mContext);

        tv.setText((position+1) + " 월");
        tv.setTextSize(10);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setTextColor(mContext.getColor(com.nbmlon.managemodule.R.color.signature_blue));


        Uri uri;
        if((uri = mUris.get(position)) != null)
            iv.setImageURI(uri);

        ll.addView(tv);
        ll.addView(iv);



        return ll;
    }
}
