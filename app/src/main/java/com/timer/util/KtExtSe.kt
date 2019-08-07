package com.timer.util

import android.util.Log
import java.util.concurrent.TimeUnit

fun Any.i(tag : String = ""){
    Log.i("$tag#$#",this.toString())
}

fun Long.toTimeStr():String{
    return String.format("%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
        TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this)))
}

/**
 * for easy convenient convert, milliseconds is long, need to multiply 1000
 */
fun Int.x1000L():Long {
    return this*1000L
}
