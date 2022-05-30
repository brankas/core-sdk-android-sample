package com.brankas.testapp.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.brankas.testapp.extension.invisible
import com.brankas.testapp.extension.visible

object CustomViewBinding {
    @BindingAdapter("setVisible")
    @JvmStatic
    fun bindVisibilityView(view: View, isVisible: Boolean) {
        if(isVisible)
            view.visible()
        else
            view.invisible()
    }
}