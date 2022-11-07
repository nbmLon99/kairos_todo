package com.nbmlon.submodule.datepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;

/**
 * Created by Jhonny Barrios on 22/02/2017.
 * Modified by nbmLon99 on 18/09/2022.
 */

public class RecyclerViewForHorizontalPicker extends RecyclerView implements OnItemClickedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private AdapterForHorizontalPicker adapter;
    private int lastPosition;
    private LinearLayoutManager layoutManager;
    private float itemWidth;
    private HorizontalPickerListener listener;

    public RecyclerViewForHorizontalPicker(Context context) {
        super(context);
    }

    public RecyclerViewForHorizontalPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewForHorizontalPicker(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void init(DateTime dateForPicker,Context context) {
        layoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);

        setAdapter(new Adapter() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            }
            @Override
            public int getItemCount() {
                return 0;
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                itemWidth=getMeasuredWidth()/7;
                adapter=new AdapterForHorizontalPicker(dateForPicker,
                        (int) itemWidth,
                        RecyclerViewForHorizontalPicker.this,
                        getContext());
                setAdapter(adapter);

                //스냅하게 도와주는거네 -->자동맞춤?
                LinearSnapHelper snapHelper=new LinearSnapHelper();
                snapHelper.attachToRecyclerView(RecyclerViewForHorizontalPicker.this);


                removeOnScrollListener(onScrollListener);
                addOnScrollListener(onScrollListener);
            }
        });
    }

    private OnScrollListener onScrollListener=new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState){
                case RecyclerView.SCROLL_STATE_IDLE:
                    listener.onStopDraggingPicker();
                    int position = (int) ((computeHorizontalScrollOffset()/itemWidth)+3.5);
                    if(position!=-1 && position!=lastPosition)
                    {
                        selectItem(true,position);
                        selectItem(false,lastPosition);
                        lastPosition=position;
                    }
                    break;
                case SCROLL_STATE_DRAGGING:
                    listener.onDraggingPicker();
            break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    private void selectItem(boolean isSelected,int position) {
        adapter.getItem(position).setSelected(isSelected);
        adapter.notifyItemChanged(position);
        if(isSelected)
        {
            listener.onDateSelected(adapter.getItem(position));
        }
    }


    public void setListener(HorizontalPickerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClickView(View v, int adapterPosition) {
        if(adapterPosition!=lastPosition && !adapter.isEMPTY(adapterPosition))
        {
            selectItem(true,adapterPosition);
            selectItem(false,lastPosition);
            lastPosition=adapterPosition;
            smoothScrollToPosition(adapterPosition);
        }
    }



    //tvToday 클릭 시
    @Override
    public void onClick(View v) {
        setDate(new DateTime());
    }

    @Override
    public void smoothScrollToPosition(int position) {
        final SmoothScroller smoothScroller = new CenterSmoothScroller(getContext());
        smoothScroller.setTargetPosition(position);
        post(new Runnable() {
            @Override
            public void run() {
                layoutManager.startSmoothScroll(smoothScroller);
            }
        });
    }

    public void setDate(DateTime date) {
        if(date.getMonthOfYear() != adapter.mMonth)
            adapter.ChangeMonth(date);
        smoothScrollToPosition(2 + date.dayOfMonth().get());
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        DateTime selectedDate = new DateTime(year, month+1 , day,0,0,0);
        setDate(selectedDate);
    }

    private static class CenterSmoothScroller extends LinearSmoothScroller {

        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
    }
}