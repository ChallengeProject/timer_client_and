package kr.co.seoft.two_min.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "time_set")

data class TimeSet(
    var title: String = "",
    var times: List<Time>,
    val memo: String = "",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "time_set_id")
    val timeSetId: Int = 0
) : Parcelable

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
    val timeId: Int = 0
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
    val bellId: Int = 0
) : Parcelable {
    enum class Type(key: Int) {
        SLIENT(0),
        VIBRATION(1),
        DEFAULT(2),
        USER(3)
    }
}

@Parcelize
data class UseInfo(
    val ymdString: String,
    val startTimeString: String,
    val endTImeString: String
) : Parcelable