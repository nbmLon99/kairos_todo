package com.nbmlon.managemodule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapManager {

    /** compress bitmap into string **/
    public static String BitmapToString(Bitmap bm){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] bytes =  stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /** decode string to bitmap **/
    public static Bitmap StringToBitmap(String str){
        byte[] encodeByte = Base64.decode(str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

}
