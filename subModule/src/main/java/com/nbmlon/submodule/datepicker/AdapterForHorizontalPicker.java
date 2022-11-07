package com.nbmlon.submodule.datepicker;


import android.app.AlarmManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.submodule.R;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by jhonn on 22/02/2017.
 * Modified by nbmLon99 on 18/09/2022
 */

public class AdapterForHorizontalPicker extends RecyclerView.Adapter<AdapterForHorizontalPicker.ViewHolder> {

    private static final long DAY_MILLIS = AlarmManager.INTERVAL_DAY;
    private int itemWidth;
    private final OnItemClickedListener listener;

    public int mMonth;
    private ArrayList<Day> items;

    private final int selectedTextColor;
    private final int mBackgroundColor;
    private final int mTextColor;
    private final int mTodayDateTextColor;

    public AdapterForHorizontalPicker(DateTime dateForPicker, int itemWidth, OnItemClickedListener listener, Context context) {
        items=new ArrayList<>();

        mBackgroundColor = context.getColor(com.nbmlon.managemodule.R.color.mainColor);
        mTodayDateTextColor = context.getColor(com.nbmlon.managemodule.R.color.signature_blue);
        selectedTextColor = context.getColor(com.nbmlon.managemodule.R.color.mainColor);
        mTextColor = context.getColor(com.nbmlon.managemodule.R.color.mainTextColor);


        this.itemWidth=itemWidth;
        this.listener=listener;
        generateDays(dateForPicker ,false);
    }


    public void ChangeMonth(DateTime DateForChange){
        generateDays(DateForChange,true);
        notifyDataSetChanged();
    }

    public  void generateDays(DateTime SelectedDay, boolean cleanArray) {
        this.mMonth = SelectedDay.getMonthOfYear();
        long initialDate = SelectedDay.dayOfMonth().withMinimumValue().getMillis();
        int n = SelectedDay.dayOfMonth().getMaximumValue();
        if(cleanArray)
            items.clear();
        for(int j = 0; j<3; j++) items.add(new Day());
        int i=0;
        while(i<n)
        {
            DateTime actualDate = new DateTime(initialDate + (DAY_MILLIS * i++));
            items.add(new Day(actualDate));
        }
        for(int j = 0; j<3; j++) items.add(new Day());

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_day,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Day item=getItem(position);

        holder.tvDay.setText(item.getDay());
        holder.tvWeekDay.setText(item.getWeekDay());

        if(item.isSelected())
        {
            holder.tvDay.setBackground(getDaySelectedBackground(holder.itemView));
            holder.tvDay.setTextColor(selectedTextColor);
        }
        else if(item.isToday())
        {
            holder.tvDay.setTextColor(mTodayDateTextColor);
            holder.tvDay.setBackgroundColor(mBackgroundColor);
        }
        else
        {
            holder.tvDay.setTextColor(mTextColor);
            holder.tvDay.setBackgroundColor(mBackgroundColor);
        }
    }

    private Drawable getDaySelectedBackground(View view) {
        Drawable drawable = view.getResources().getDrawable(R.drawable.background_day_selected,null);
        return drawable;
    }


    public Day getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDay,tvWeekDay;
        LinearLayout baseDay;

        public ViewHolder(View itemView) {
            super(itemView);
            baseDay = (LinearLayout) itemView.findViewById(R.id.baseDay);
            baseDay.setBackgroundColor(mBackgroundColor);

            tvDay= (TextView) itemView.findViewById(R.id.tvDay);
            tvDay.setTextColor(mTextColor);
            tvDay.setWidth(itemWidth);

            tvWeekDay= (TextView) itemView.findViewById(R.id.tvWeekDay);
            tvWeekDay.setTextColor(mTextColor);

            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            listener.onClickView(v, getAdapterPosition());

        }
    }

    public Boolean isEMPTY(int position) { return items.get(position).isEMPTY();}



}