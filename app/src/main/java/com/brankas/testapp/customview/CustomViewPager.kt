package com.brankas.testapp.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

/**
 * Custom [ViewPager] that disables any touch or swipe
 *
 */
class CustomViewPager : ViewPager {
    var isTouchEnabled = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isTouchEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isTouchEnabled && super.onInterceptTouchEvent(event)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return isTouchEnabled && super.canScrollHorizontally(direction)
    }
}
