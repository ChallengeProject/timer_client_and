package com.timer.history

import com.timer.TimerApp
import com.timer.db.entity.TempHistoryEntity

object HistoryRepository {

    private val database = (TimerApp.application as TimerApp).getDatabase()

    fun saveTempHistories(histories: List<TempHistoryEntity>) {
        database.historyDao().insertHistories(histories)
    }

    fun loadHistories() = database.historyDao().loadHistories()
}