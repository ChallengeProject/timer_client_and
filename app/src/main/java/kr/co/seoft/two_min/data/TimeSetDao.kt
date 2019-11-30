package kr.co.seoft.two_min.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single

@Dao
interface TimeSetDao {
    @Insert
    fun insertTimeSet(time: TimeSet)

    @Query("SELECT * FROM time_set")
    fun getTimeSets(): Single<List<TimeSet>>
}
