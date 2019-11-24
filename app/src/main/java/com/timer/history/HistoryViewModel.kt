package com.timer.history

import androidx.lifecycle.LiveData
import com.timer.db.entity.TempHistoryEntity

class HistoryViewModel {

    var histories: LiveData<List<TempHistoryEntity>> = HistoryRepository.loadHistories()

    init {
        saveTempData()
    }

    fun saveTempData() {
        HistoryRepository.saveTempHistories(listOf(TempHistoryEntity(), TempHistoryEntity(), TempHistoryEntity()))
    }
}