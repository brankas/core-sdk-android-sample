package com.brankas.testapp.custom;

import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

public class SvgSoftwareLayerSetter  implements RequestListener {

    @Override
    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        ImageView view = (ImageView)((ImageViewTarget)target).getView();
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null);
        }
        return false;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        ImageView view = (ImageView)((ImageViewTarget)target).getView();
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
        }
        return false;
    }
}
