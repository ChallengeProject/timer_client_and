package kr.co.seoft.two_min.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Time::class,
        TimeSet::class,
        History::class,
        Bell::class
    ],
    version = 1
)

@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeSetDao(): TimeSetDao

    companion object {
        private lateinit var INSTANCE: AppDatabase
        var isUse = false
        fun getDatabase(context: Context, testMode: Boolean = false): AppDatabase {
            if (!isUse) {
                isUse = true
                synchronized(AppDatabase::class) {
                    INSTANCE = if (testMode) {
                        Room.inMemoryDatabaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java
                        ).build()
                    } else {
                        Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "std-of-android.db"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}