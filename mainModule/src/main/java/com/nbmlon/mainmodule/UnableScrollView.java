package com.nbmlon.mainmodule;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class UnableScrollView extends ScrollView {
    public UnableScrollView(Context context) {
        super(context);
    }

    public UnableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
