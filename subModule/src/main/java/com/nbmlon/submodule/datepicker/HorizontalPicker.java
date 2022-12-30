package com.nbmlon.submodule.datepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nbmlon.submodule.R;

import org.joda.time.DateTime;

/**
 * Created by Jhonny Barrios on 22/02/2017.
 * Modified by nbmLon99 on 18/09/2022
 */

public class HorizontalPicker extends LinearLayout implements HorizontalPickerListener {

    private static final int NO_SETTED = -1;
    private View vHover;
    private TextView tvMonth;
    private TextView tvToday;
    private LinearLayout baseView;
    private ImageView icon_search, icon_add;
    private DatePickerListener listener;
    private IconClickListener listener_icon;
    private RecyclerViewForHorizontalPicker rvDays;
    private boolean showTodayButton = false;


    private final String mMonthPattern = "YYYY년 MM월 dd일";
    private final int mTodayDateTextColor = getColor(com.nbmlon.managemodule.R.color.signature_blue);
    private final int mTextColor = getColor(com.nbmlon.managemodule.R.color.mainTextColor);
    private final int mBackgroundColor = getColor(com.nbmlon.managemodule.R.color.mainColor);

    private DateTime selectedDate;

    public HorizontalPicker(Context context) {
        super(context);
    }

    public HorizontalPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public HorizontalPicker(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public HorizontalPicker setListener(DatePickerListener listener) {
        this.listener = listener;
        return this;
    }


    public void setDate(final DateTime date) {
        rvDays.post(new Runnable() {
            @Override
            public void run() {
                rvDays.setDate(date);
            }
        });
    }


    //view holder 역할?
    public void init() {
        inflate(getContext(), R.layout.horizontal_picker, this);
        rvDays = (com.nbmlon.submodule.datepicker.RecyclerViewForHorizontalPicker) findViewById(R.id.rvDays);
        vHover = findViewById(R.id.vHover);
        tvMonth = (TextView) findViewById(R.id.tvMonth);

        baseView = (LinearLayout) findViewById(R.id.baseView);
        baseView.setBackgroundColor(mBackgroundColor);
        tvMonth.setTextColor(mTextColor);

        new Thread(this::InitIconListener).start();


        tvToday = (TextView) findViewById(R.id.tvToday);
        rvDays.setListener(this);
        tvMonth.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(),
                    com.nbmlon.dialogmodule.R.style.MyDialogTheme ,
                    rvDays,
                    selectedDate.getYear(),
                    selectedDate.getMonthOfYear()-1,
                    selectedDate.getDayOfMonth());
            dialog.show();
        });
        
        //tvToday의 클릭을 rvDays가 기다리게함
        tvToday.setOnClickListener(rvDays);
        tvToday.setVisibility(showTodayButton ? VISIBLE : GONE);

        new Thread(() ->
                rvDays.init(
                    new DateTime(),
                    getContext()
                )
        ).run();



    }

    private void InitIconListener(){
        icon_add = (ImageView) findViewById(R.id.icon_add);
        icon_search = (ImageView) findViewById(R.id.icon_search);


        icon_add.setOnClickListener(view -> listener_icon.OnAddIconClick());

        icon_search.setOnClickListener(view -> listener_icon.OnSearchIconClick());


    }

    private int getColor(int colorId) {
        return getContext().getColor(colorId);
    }


    @Override
    public boolean post(Runnable action) { return rvDays.post(action); }





    @Override
    public void onStopDraggingPicker() {
        if (vHover.getVisibility() == VISIBLE)
            vHover.setVisibility(INVISIBLE);
    }

    @Override
    public void onDraggingPicker() {
        if (vHover.getVisibility() == INVISIBLE)
            vHover.setVisibility(VISIBLE);
    }



    @Override
    public void onDateSelected(Day item) {
        selectedDate = item.getDate();
        if (listener != null) {
            listener.onDateSelected(selectedDate);
            tvMonth.setText(item.getMonth(mMonthPattern));
            icon_add.setVisibility(item.isFuture() ? VISIBLE : INVISIBLE);
            tvToday.setVisibility(item.isToday() ? INVISIBLE : VISIBLE);
        }
    }


    public HorizontalPicker setListener_icon(com.nbmlon.submodule.datepicker.IconClickListener listener_icon) {
        this.listener_icon = listener_icon;
        return this;
    }
}