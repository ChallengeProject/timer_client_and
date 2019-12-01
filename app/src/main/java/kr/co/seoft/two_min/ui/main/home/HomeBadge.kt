package kr.co.seoft.two_min.ui.main.home

import kr.co.seoft.two_min.data.Time

data class HomeBadge(
    var time: Time,
    var type: HomeBadgeType
) {
    fun getStringUsingFormat(): String {
        var hour = time.seconds / 3600
        var min = time.seconds / 60 % 60
        var second_ = time.seconds % 60

        return "${if(hour<10) "0$hour" else hour}:${if(min<10) "0$min" else min}:${if(second_<10) "0$second_" else second_}"
    }
}

enum class HomeBadgeType {
    EMPTY,
    FOCUS,
    NORMAL,
    ADD_SHOW,
    ADD_HIDE,
    REPEAT_ON,
    REPEAT_OFF
}

enum class HomeBadgeCallbackType {
    NORMAL_PUSH,
    LONG_PUSH,
    ADD_PUSH,
    FOCUS_PUSH,
    REPEAT_ON_PUSH,
    REPEAT_OFF_PUSH,
}

