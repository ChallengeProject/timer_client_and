package com.timer.se_data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeSet(
    var title: String = "",
    val readySecond: Int,
    var times: List<Time>,
    val memo: String = ""
) : Parcelable

@Parcelize
data class Time(
    var seconds: Int,
    var comment: String = "",
    var bell: Bell = Bell(Bell.Type.DEFAULT, null)
) : Parcelable

@Parcelize
data class Bell(
    var type: Type,
    var uri: Uri?
) : Parcelable {
    enum class Type {
        SLIENT,
        VIBRATION,
        DEFAULT,
        USER
    }
}

@Parcelize
data class UseInfo(
    val ymdString : String,
    val startTimeString : String,
    val endTImeString :String
) : Parcelable