package kr.co.seoft.two_min.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "history")
data class History(
    @ColumnInfo(name = "time_set_id")
    val timeSetId: Long,
    @ColumnInfo(name = "whole_time")
    val wholeTime: Int,
    @ColumnInfo(name = "time_set_title")
    val timeSetTitle: String,
    @ColumnInfo(name = "ymd_string")
    val ymdString: String,
    @ColumnInfo(name = "start_time_string")
    val startTimeString: String,
    @ColumnInfo(name = "end_time_string")
    val endTimeString: String,
    @ColumnInfo(name = "add_minute")
    val addMinute: Int = 0,
    @ColumnInfo(name = "repeat_count")
    val repeatCount: Int = 0,
    @ColumnInfo(name = "pause_count")
    val pauseCount: Int = 0,
    @ColumnInfo(name = "change_count")
    val changeCount: Int = 0,
    @ColumnInfo(name = "cancel_info")
    val cancelInfo: String = "", // 양식 파싱해서 쓰자 : N#00:00:00
    @ColumnInfo(name = "exceed_second")
    val exceedSecond: Int = 0,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "history_id")
    var historyId: Long = 0
) : Parcelable

@Parcelize
@Entity(tableName = "time_set")
data class TimeSet(
    var title: String = "",
    var times: List<Time>,
    val memo: String = "",
    var saveOrder: Int = -1,
    var likeOrder: Int = -1,
    var useHistory: Int = 0,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "time_set_id")
    var timeSetId: Long = 0
) : Parcelable {

    var wholeTime: Int = 0
        get() {
            return times.map { it.seconds }.reduce { acc, i -> acc + i }
        }
}

@Parcelize
@Entity(
    tableName = "time",
    foreignKeys = [
        ForeignKey(
            entity = TimeSet::class,
            parentColumns = ["time_set_id"],
            childColumns = ["time_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Time(
    var seconds: Int,
    var comment: String = "",

    @Embedded
    var bell: Bell = Bell(Bell.Type.DEFAULT, null),

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "time_id")
    val timeId: Long = 0
) : Parcelable

@Parcelize
@Entity(
    tableName = "bell",
    foreignKeys = [
        ForeignKey(
            entity = Time::class,
            parentColumns = ["time_id"],
            childColumns = ["bell_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Bell(
    var type: Type,
    var uriStr: String? = null,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bell_id")
    val bellId: Long = 0
) : Parcelable {
    enum class Type(key: Int) {
        SLIENT(0),
        VIBRATION(1),
        DEFAULT(2),
        USER(3)
    }

    fun bellTypeToString(): String {
        return when (type) {
            Type.SLIENT -> "무음"
            Type.VIBRATION -> "진동"
            Type.DEFAULT -> "기본음"
            else -> "기본음"
        }
    }
}

@Parcelize
data class UseInfo(
    val ymdString: String,
    val startTimeString: String,
    val endTimeString: String,
    val addMinute: Int = 0,
    val repeatCount: Int = 0,
    val pauseCount: Int = 0,
    val changeCount: Int = 0,
    var cancelInfo: String = "", // 양식 파싱해서 쓰자 : N#00:00:00
    var exceedSecond: Int = 0
) : Parcelable