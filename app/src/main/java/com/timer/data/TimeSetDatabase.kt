package com.timer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.timer.se_data.TimeSet

@Database(entities = arrayOf(TimeSet::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeSetDao(): TimeSetDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "your_db.db").build()
                }
            }
            return INSTANCE
        }
    }
}