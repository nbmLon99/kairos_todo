package com.nbmlon.customcalender_myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nbmlon.customcalender_myfirstapp.tutorial.TutorialActivity;
import com.nbmlon.mainmodule.mainimage.myCalender;
import com.nbmlon.managemodule.FileManager;
import com.nbmlon.managemodule.SharedPreferenceManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_TIME = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_window);
        JodaTimeAndroid.init(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileManager.Gallery fm = new FileManager.Gallery(getApplicationContext());
                CustomCalender.RESULT_MONTHS = fm.getPossibleGalleryYear();

                int win_color = getColor(com.nbmlon.managemodule.R.color.WIN_COLOR);
                int drw_color = getColor(com.nbmlon.managemodule.R.color.DRW_COLOR);
                int los_color = getColor(com.nbmlon.managemodule.R.color.LOS_COLOR);
                int today_color = getColor(com.nbmlon.managemodule.R.color.TODAY_COLOR);

                myCalender.CalenderSetting(win_color, drw_color, los_color, today_color);

            }
        }).run();

        SharedPreferenceManager.DayLoader dayLoader = new SharedPreferenceManager.DayLoader(this);
        CustomCalender.LastAccessYearMonth = dayLoader.getDay();
        int TodaysYearMonth = Integer.parseInt(new DateTime().toString("yyyyMM"));

        if(CustomCalender.LastAccessYearMonth == CustomCalender.UNSETTING){
            CustomCalender.LastAccessYearMonth = TodaysYearMonth;
            dayLoader.saveDay(CustomCalender.LastAccessYearMonth);
            FirstAccess();
        }
        else{
            Intent intent =  new Intent(getApplication(), MainActivity.class);

            if (CustomCalender.LastAccessYearMonth != TodaysYearMonth){
                intent.putExtra("YearMonthChanged", true);
                intent.putExtra("yearMonth", TodaysYearMonth );
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(intent);
                SplashActivity.this.finish();
            }, SPLASH_DISPLAY_TIME);


        }

    }

    private void FirstAccess(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(getApplication(), TutorialActivity.class));
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_TIME);
    }


    @Override
    public void onBackPressed() {

    }


}
