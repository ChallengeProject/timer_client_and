package com.timer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.timer.db.dao.HistoryDao
import com.timer.db.entity.TempHistoryEntity


@Database(version = 1, entities = [TempHistoryEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}