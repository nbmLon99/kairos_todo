package com.nbmlon.managemodule;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Toast;

public class ViewCropperBitmap {

    /** View 화면을 캡쳐하여 Bitmap 반환 **/
    public static Bitmap getBitmapFromView(Activity CallingActivity, View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        int[] location = new int[2];
        view.getLocationInWindow(location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rect rect = new Rect(location[0],location[1],location[0]+view.getWidth(),location[1]+view.getHeight());

            PixelCopy.request(CallingActivity.getWindow(),rect,returnedBitmap,
                    copyResult -> {
                        if(copyResult != PixelCopy.SUCCESS)
                            Toast.makeText(CallingActivity.getApplicationContext(),"cannot crop",Toast.LENGTH_SHORT).show();
                    },
                    new Handler(Looper.getMainLooper())
            );

        }
        else{
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable =view.getBackground();
            if (bgDrawable!=null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            view.draw(canvas);

        }

        return returnedBitmap;
    }
}
