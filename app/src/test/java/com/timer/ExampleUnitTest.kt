package com.timer

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun test001() {

        println("a ${Calendar.getInstance().get(Calendar.YEAR)}")
        println("b ${Calendar.getInstance().get(Calendar.MONTH)}")
        println("c ${Calendar.getInstance().get(Calendar.DATE)}")

        println("d ${Calendar.getInstance().get(Calendar.AM_PM)}")
        println("e ${Calendar.getInstance().get(Calendar.HOUR)}")
        println("f ${Calendar.getInstance().get(Calendar.MINUTE)}")

    }


}
