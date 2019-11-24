package com.timer.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Temp")
class TempHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    var id = null
}