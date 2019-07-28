package com.timer.util

import android.util.Log

fun Any.i(tag : String = "#$#"){
    Log.i(tag,this.toString())
}