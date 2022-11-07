package com.nbmlon.managemodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class SharedPreferenceManager {

    public static class BitmapLoader{
        private final Context mContext;
        private final SharedPreferences sp;

        public BitmapLoader(Context context, int yyyyMM){
            mContext = context;
            String spName = yyyyMM + "_MainImage";
            sp = mContext.getSharedPreferences(spName,Context.MODE_PRIVATE);
        }


        public void saveBitmap(Bitmap bm){
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("MainImage", BitmapManager.BitmapToString(bm));
            editor.commit();
        }

        public Bitmap getBitmap(int df_resID){
            Bitmap bm;
            String str = sp.getString("MainImage","NO_IMAGE");
            if (str.equals("NO_IMAGE"))
                bm = BitmapFactory.decodeResource(mContext.getResources(), df_resID);
            else
                bm = BitmapManager.StringToBitmap(str);
            return bm;
        }



        public void clear(){
            sp.edit().clear().commit();
        }

    }

    /**
     *  TRY_MARKed  means   todos exist at least one
     *  DRW_MARKed  means   TRY_MARKed and todos Done exist at least one
     *  WIN_MARKed  means   DRW_MARKed and todos Lefted doesnt exist
     * **/

    public static class MarkersLoader{
        public final int TRY_MARKER_INDEX = 0;
        public final int DRW_MARKER_INDEX = 1;
        public final int WIN_MARKER_INDEX = 2;


        private final SharedPreferences sp;


        public MarkersLoader(Context context, int yyyyMM){
            String spName = yyyyMM + "_MARKER";
            sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        }



        public void saveMarkers(int win_marker, int drw_marker, int try_marker){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("WIN_MARKER", win_marker);
            editor.putInt("DRW_MARKER", drw_marker);
            editor.putInt("TRY_MARKER", try_marker);

            editor.commit();
        }

        public int[] getMarkers(){
            int[] res = new int[3];
            res[TRY_MARKER_INDEX] = sp.getInt("TRY_MARKER", 0b0);
            res[DRW_MARKER_INDEX] = sp.getInt("DRW_MARKER", 0b0);
            res[WIN_MARKER_INDEX] = sp.getInt("WIN_MARKER", 0b0);
            return res;
        }

        public void clear(){
            sp.edit().clear().commit();
        }
    }


    public static class DayLoader{
        private final SharedPreferences sp;

        public DayLoader(Context context){
            String spName = "DayLoader";
            sp = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        }


        public void saveDay(Integer LastAccessYearMonth){
            ((Runnable) () -> {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("LastAccessYearMonth", LastAccessYearMonth);
                editor.commit();
            }).run();
        }

        public int getDay(){
            return sp.getInt("LastAccessYearMonth", -1);
        }
    }

}
