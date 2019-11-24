package com.timer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timer.db.entity.TempHistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM `Temp`")
    fun loadHistories(): LiveData<List<TempHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistories(histories: List<TempHistoryEntity>)
}