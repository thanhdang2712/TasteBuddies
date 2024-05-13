package hu.ait.tastebuddies.utils

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun parseMonthDay(date: Date): String {
    val dateFormat = SimpleDateFormat("MMMM d") // Define the format to parse month and day
    return dateFormat.format(date)
}