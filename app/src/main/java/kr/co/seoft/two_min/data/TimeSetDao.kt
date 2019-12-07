package kr.co.seoft.two_min.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single

@Dao
interface TimeSetDao {
    @Insert
    fun insertTimeSet(time: TimeSet): Single<Long>

    @Query("SELECT * FROM time_set")
    fun getTimeSets(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set ORDER BY likeOrder DESC, time_set_id ASC")
    fun getSaveTimeSets(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set WHERE time_set_id=:id")
    fun getTimeSetById(id: Long): Single<TimeSet>
}
