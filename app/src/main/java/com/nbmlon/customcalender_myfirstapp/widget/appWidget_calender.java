package com.nbmlon.customcalender_myfirstapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.nbmlon.customcalender_myfirstapp.R;
import com.nbmlon.customcalender_myfirstapp.SplashActivity;
import com.nbmlon.mainmodule.mainimage.myCalender;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class appWidget_calender extends AppWidgetProvider {

    static List<Integer> enableIDs = new ArrayList<>();

    private static boolean UNSETTED = true;
    private static boolean IMAGE_UPDATED = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        int time = new DateTime().getHourOfDay();

        if (UNSETTED || IMAGE_UPDATED || (time == 0)) {
            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.app_widget_cal);


            int MAX_WIDTH = (int) (320 * context.getResources().getDisplayMetrics().density + 0.5f);
            myCalender.curCalender curCalender = new myCalender.curCalender(context, MAX_WIDTH);
            widgetView.setImageViewBitmap(R.id.appWidget_iv ,curCalender.getCalender());

            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            widgetView.setOnClickPendingIntent(R.id.appWidget_root, pi);

            appWidgetManager.updateAppWidget(appWidgetId, widgetView);
            UNSETTED = false;
            IMAGE_UPDATED = false;
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            if (!enableIDs.contains(appWidgetId))
                UNSETTED = true;

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        enableIDs = new ArrayList<>() ;
        for (int appWidgetId : appWidgetIds)
            enableIDs.add(appWidgetId);
    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
    public static void cal_imgupdated(){
        IMAGE_UPDATED = true;
    }



}