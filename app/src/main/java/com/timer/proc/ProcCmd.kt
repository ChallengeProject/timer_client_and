package com.timer.proc

object CMD_BRD {

    const val TIME = "TIME"
    const val ROUND = "ROUND"
    const val END = "END"
    const val STOP = "STOP"
    const val MSG = "MSG"
    const val REMAIN_SEC = "REMAIN_SEC"
    const val UPDATE_REPEAT_BTN = "UPDATE_REPEAT_BTN"
    const val UPDATE_REPEAT_CNT = "UPDATE_REPEAT_CNT"

}

object CMD_SERVICE {
    const val PAUSE = "PAUSE"
    const val RESTART = "RESTART"
    const val STOP = "STOP"
    const val SOUND_OFF = "SOUND_OFF"
    const val START_WITH_TIMERS = "START_WITH_TIMERS"

}

enum class ProcStatus{
    READY,
    ING,
    PAUSE
}