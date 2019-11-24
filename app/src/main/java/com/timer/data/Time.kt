package com.timer.se_data

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "time_set")
data class TimeSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
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
        SILENT,
        VIBRATION,
        DEFAULT,
        USER
    }
}

@Parcelize
data class UseInfo(
    val ymdString: String,
    val startTimeString: String,
    val endTImeString: String
) : Parcelable