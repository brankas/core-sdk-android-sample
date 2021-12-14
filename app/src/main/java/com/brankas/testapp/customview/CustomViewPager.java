package com.brankas.testapp.customview;

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Custom [ViewPager] that disables any touch or swipe
 *
 */
public class CustomViewPager extends ViewPager {
    private boolean isTouchEnabled = false;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isTouchEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isTouchEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return isTouchEnabled && super.canScrollHorizontally(direction);
    }

    public boolean isTouchEnabled() {
        return isTouchEnabled;
    }

    public void setTouchEnabled(boolean touchEnabled) {
        isTouchEnabled = touchEnabled;
    }
}
