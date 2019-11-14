package com.timer.proc

import java.util.*

// ref : https://stackoverflow.com/questions/23323823/android-countdowntimer-tick-is-not-accurate
abstract class PreciseCountdown @JvmOverloads constructor(
    private val totalTime: Long,
    private val interval: Long,
    private val delay: Long = 0
) : Timer("PreciseCountdown", true) {
    private var task: TimerTask? = null
    private var startTime: Long = -1
    private var restart = false
    private var wasCancelled = false
    private var wasStarted = false

    init {
        this.task = getTask(totalTime)
    }

    fun start() {
        wasStarted = true
        this.scheduleAtFixedRate(task, delay, interval)
    }

    fun restart() {
        if (!wasStarted) {
            start()
        } else if (wasCancelled) {
            wasCancelled = false
            this.task = getTask(totalTime)
            start()
        } else {
            this.restart = true
        }
    }

    fun stop() {
        this.wasCancelled = true
        this.task!!.cancel()
    }

    // Call this when there's no further use for this timer
    fun dispose() {
        cancel()
        purge()
    }

    private fun getTask(totalTime: Long): TimerTask {
        return object : TimerTask() {

            override fun run() {
                val timeLeft: Long
                if (startTime < 0 || restart) {
                    startTime = scheduledExecutionTime()
                    timeLeft = totalTime
                    restart = false
                } else {
                    timeLeft = totalTime - (scheduledExecutionTime() - startTime)

                    if (timeLeft <= 0) {
                        this.cancel()
                        startTime = -1
                        onFinished()
                        return
                    }
                }

                onTick(timeLeft)
            }
        }
    }

    abstract fun onTick(timeLeft: Long)
    abstract fun onFinished()
}