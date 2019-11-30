package kr.co.seoft.two_min

import androidx.test.rule.ActivityTestRule
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.Time
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.data.TimeSetDao
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.e
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class RoomTest {
    private lateinit var timeSetDao: TimeSetDao
    private lateinit var db: AppDatabase

    @JvmField
    @Rule
    val main = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        val context = main.activity
        db = AppDatabase.getDatabase(context, true)
        timeSetDao = db.timeSetDao()
    }

    @Test
    fun TimeSetTest() {


        val time1 = Time(1111)
        val time2 = Time(2222)
        val time3 = Time(3333)
        val time4 = Time(4444)

        val timeSet1 = TimeSet("TITLE 1", listOf(time1, time2))
        val timeSet2 = TimeSet("TITLE 1", listOf(time3, time4))
        timeSetDao.insertTimeSet(timeSet1)
        timeSetDao.insertTimeSet(timeSet2)

        timeSetDao.getTimeSets().subscribe({
            it.e()
            Assert.assertEquals(2, it.size)
        }, {
            it.printStackTrace()
        })

    }
}