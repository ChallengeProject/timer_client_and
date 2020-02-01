package kr.co.seoft.two_min.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Single

/**
 * 히스토리 저장시 히스토리 테이블에 쌓이고 타임셋 테이블도 쌓이고(useHistory) 참조한다
 */
@Dao
interface TimeSetDao {
    @Insert
    fun insertTimeSet(time: TimeSet): Single<Long>

    @Query("SELECT * FROM time_set WHERE useHistory=0")
    fun getTimeSets(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set WHERE useHistory=0 ORDER BY saveOrder ASC, time_set_id DESC")
    fun getTimeSetsOrderBySave(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set WHERE useHistory=0 AND NOT likeOrder=-1 ORDER BY likeOrder ASC, time_set_id DESC")
    fun getTimeSetsOrderByLike(): Single<List<TimeSet>>

    @Query("SELECT * FROM time_set WHERE time_set_id=:id")
    fun getTimeSetById(id: Long): Single<TimeSet>

    @Insert
    fun insertHistory(history: History): Single<Long>

    @Query("SELECT * FROM history ORDER BY history_id DESC")
    fun getHistories(): Single<List<History>>

//    @Query("SELECT * FROM time_set WHERE useHistory=1 ORDER BY time_set_id DESC")
//    fun getHistoryTimeSets(): Single<List<TimeSet>>

    @Query("SELECT * FROM history WHERE time_set_id=:timeSetId")
    fun getHistoryByTimeSetId(timeSetId: Long): Single<History>

    @Query("SELECT * FROM history WHERE history_id=:historyId")
    fun getHistoryByHistoryId(historyId: Long): Single<History>

    @Update(onConflict = REPLACE)
    fun updateTimeSet(timeSet: TimeSet)

    @Delete
    fun deleteTimeSet(timeSet: TimeSet)

    @Query("DELETE FROM history WHERE time_set_id=:id")
    fun deleteTimeSetById(id: Long)

    @Delete
    fun deleteHistory(history: History)

}
