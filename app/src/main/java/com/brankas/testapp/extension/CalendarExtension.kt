package com.brankas.testapp.extension

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.getDateString() : String {
    val format = SimpleDateFormat("MMMM d yyyy", Locale.getDefault())
    return format.format(timeInMillis)
}