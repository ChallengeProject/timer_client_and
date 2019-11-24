package com.timer

import android.app.Application
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.timer.db.AppDatabase

class TimerApp : MultiDexApplication() {
    private lateinit var database: AppDatabase

    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()

        application = this
    }

    fun getDatabase(): AppDatabase {
        if (!::database.isInitialized) {
            val executors = AppExecutors()
            database = Room.databaseBuilder(application, AppDatabase::class.java, "timer.db")
                .fallbackToDestructiveMigration()
                .setQueryExecutor(executors.diskIO())
                .setTransactionExecutor(executors.diskIO())
                .build()
        }

        return database
    }
}