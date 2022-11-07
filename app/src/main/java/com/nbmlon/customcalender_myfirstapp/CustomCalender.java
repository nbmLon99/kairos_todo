package com.nbmlon.customcalender_myfirstapp;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class CustomCalender extends Application {
    public static final int UNSETTING = -1;

    public static int mainImageLength = UNSETTING;
    public static int LastAccessYearMonth = UNSETTING;
    public static ArrayList<Integer> RESULT_MONTHS = new ArrayList<>();


    public static void getAds(Context context, AdView adView){
        MobileAds.initialize(context, initializationStatus -> { });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
