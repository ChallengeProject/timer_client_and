package com.timer.data

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single

@Dao
interface HistoryDao :BaseDao<History> {
    @Query("SELECT * FROM history WHERE id = :id")
    fun selectById(id: Int): Single<History>

    @Query("SELECT * FROM history")
    fun selectAll(): Single<List<History>>

}