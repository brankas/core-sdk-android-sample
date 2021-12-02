package com.brankas.testapp.`interface`

import android.os.Parcel
import android.os.Parcelable

interface ScreenListener: Parcelable {
    fun onFieldsFilled(isFilled: Boolean, map: HashMap<String, String>, page: Int)
    override fun writeToParcel(p0: Parcel?, p1: Int) {}
    override fun describeContents() = 0
}