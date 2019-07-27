package com.timer.util

data class TimesetBadge(
    var number: Int = -1,
    var second: Int,
    var type: TimesetBadgeType
) {
    fun getStringUsingFormat(): String {
        var hour = second / 3600
        var min = second / 60 % 60
        var second_ = second % 60

        return "${if(hour<10) "0$hour" else hour}:${if(min<10) "0$min" else min}:${if(second_<10) "0$second_" else second_}"
    }
}

enum class TimesetBadgeType {
    FOCUS,
    FOCUS_WITH_TOP_ICON,
    NORMAL,
}