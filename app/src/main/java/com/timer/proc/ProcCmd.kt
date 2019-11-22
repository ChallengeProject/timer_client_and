package com.timer.proc

object CMD_BRD {

    const val TIME = "TIME"
    const val ROUND = "ROUND"
    const val END = "END"
    const val STOP = "STOP"
    const val MSG = "MSG"

    /**
     * remainSecconds in Proc
     * exceedSeconds in ExceedProc
     */
    const val REMAIN_SEC = "REMAIN_SEC"
    const val UPDATE_REPEAT_BTN = "UPDATE_REPEAT_BTN"
    const val UPDATE_REPEAT_CNT = "UPDATE_REPEAT_CNT"

}

object CMD_PROC_SERVICE {
    const val PAUSE = "PROC_PAUSE"
    const val RESTART = "PROC_RESTART"
    const val STOP = "PROC_STOP"
    const val SOUND_OFF = "PROC_SOUND_OFF"
    const val START_WITH_TIMERS = "PROC_START_WITH_TIMERS"

}

object CMD_EXCEED_SERVICE {
    const val PAUSE = "EXCEED_PAUSE"
    const val RESTART = "EXCEED_RESTART"
    const val STOP = "EXCEED_STOP"
    const val SOUND_OFF = "EXCEED_SOUND_OFF"
    const val START_WITH_TIMERS = "EXCEED_START_WITH_TIMERS"

}

enum class ProcStatus{
    READY,
    ING,
    PAUSE
}