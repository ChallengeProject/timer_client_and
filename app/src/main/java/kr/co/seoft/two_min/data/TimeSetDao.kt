package kr.co.seoft.two_min.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Single

@Dao
interface TimeSetDao {
    @Insert
    fun insertTimeSet(time: TimeSet): Single<Long>

    @Query("SELECT * FROM time_set")
    fun getTimeSets(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set ORDER BY saveOrder ASC, time_set_id DESC")
    fun getTimeSetsOrderBySave(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set WHERE NOT likeOrder=-1 ORDER BY likeOrder ASC, time_set_id DESC")
    fun getTimeSetsOrderByLike(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set WHERE time_set_id=:id")
    fun getTimeSetById(id: Long): Single<TimeSet>

    @Update(onConflict = REPLACE)
    fun updateTimeSet(timeSet: TimeSet)

    @Delete
    fun deleteTimeSet(timeSet: TimeSet)
}
