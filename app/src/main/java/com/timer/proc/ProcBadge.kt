package com.timer.proc

data class ProcBadge(
    var count: Int,
    var second: Int,
    var type : ProcBadgeType
){
    companion object {
        val emptyProcBadge = ProcBadge(-1,-1,ProcBadgeType.EMPTY)
    }

    fun getStringUsingFormat(): String {
        var hour = second / 3600
        var min = second / 60 % 60
        var second_ = second % 60

        return "${if(hour<10) "0$hour" else hour}:${if(min<10) "0$min" else min}:${if(second_<10) "0$second_" else second_}"
    }
}


enum class ProcBadgeType {
    EMPTY,
    NORMAL,
    FOCUS

}