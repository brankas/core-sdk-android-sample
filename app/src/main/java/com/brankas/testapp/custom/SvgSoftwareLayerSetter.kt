package com.brankas.testapp.custom

import android.graphics.drawable.PictureDrawable
import android.os.Build
import android.widget.ImageView
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target


class SvgSoftwareLayerSetter<T> : RequestListener<T, PictureDrawable> {
    override fun onException(
        e: Exception?,
        model: T,
        target: Target<PictureDrawable>?,
        isFirstResource: Boolean
    ): Boolean {
        val view: ImageView = (target as ImageViewTarget<*>).view
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null)
        }
        return false
    }

    override fun onResourceReady(
        resource: PictureDrawable?,
        model: T,
        target: Target<PictureDrawable>?,
        isFromMemoryCache: Boolean,
        isFirstResource: Boolean
    ): Boolean {
        val view = (target as ImageViewTarget<*>).view
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
        }
        return false
    }

}