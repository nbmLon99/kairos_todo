package com.nbmlon.submodule.datepicker;

import org.joda.time.DateTime;

import java.util.Locale;

/**
 * Created by jhonn on 28/02/2017.
 * Modified by nbmLon99 on 18/09/2022
 */
public class Day {
    private DateTime date;
    private boolean selected;
    private String monthPattern = "MMMM YYYY";

    public Day() {this.date = null;}
    public Day(DateTime date) {
        this.date = date;
    }

    public String getDay() {
        if(date == null) return null;
        return String.valueOf(date.getDayOfMonth());
    }

    public String getWeekDay() {
        if(date == null) return null;
        return date.toString("EEE", Locale.getDefault()).toUpperCase();
    }


    public String getMonth(String pattern) {
        if (!pattern.isEmpty())
            this.monthPattern = pattern;

        return date.toString(monthPattern, Locale.getDefault());
    }

    public DateTime getDate() {
        if(date == null) return null;
        return date.withTime(0,0,0,0);
    }

    public boolean isToday() {
        if(date == null) return false;

        DateTime today = new DateTime().withTime(0,0,0,0);
        return getDate().getMillis()==today.getMillis();
    }

    public boolean isFuture(){
        if(date==null) return false;

        DateTime today = new DateTime().withTime(0,0,0,0);
        return getDate().getMillis()>=today.getMillis();
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    public boolean isSelected() {
        return selected;
    }

    public boolean isEMPTY(){return date == null;}

}