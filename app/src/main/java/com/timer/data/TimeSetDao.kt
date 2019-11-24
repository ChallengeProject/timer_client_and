package com.timer.data

import androidx.room.Dao
import androidx.room.Query
import com.timer.se_data.TimeSet
import io.reactivex.Single

@Dao
interface TimeSetDao :BaseDao<TimeSet> {
    @Query("SELECT * FROM time_set WHERE id = :id")
    fun selectById(id: Int): Single<TimeSet>

    @Query("SELECT * FROM time_set")
    fun selectAll(): Single<List<TimeSet>>

}