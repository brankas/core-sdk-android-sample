package com.brankas.testapp.listener;

import android.os.Parcelable;

import java.util.HashMap;

public interface ScreenListener extends Parcelable {
    void onFieldsFilled(boolean isFilled, HashMap<String, String> map, int page);
}