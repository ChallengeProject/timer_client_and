package com.timer.proc

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.timer.se_data.TimeSet
import com.timer.se_util.BellManager
import com.timer.se_util.i
import com.timer.se_util.toTimeStr
import com.timer.se_util.x1000L
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 *
 * ref : https://github.com/seose/android-poc/tree/time-count
 *
 * LOGIC :
 *
 * 1. start service from TimingAct after readying
 * 2. start timer & show notification when bring CMD_SERVICE.START_WITH_TIMERS cmd from TimingAct
 * 3. get commands when running timer & Adjust command and send command & value to ACT or NOTI
 *
 * Last. cancelTimerStatus() & isPause = false & procService = null & sendBroadcast(Intent(CMD_BRD.STOP)) from STOP commands
 *
 * 0812
 * move :
 *
 * 1. bring command from activity with move pos
 * 2. refresh arrayCnt to move pos
 * 3. refresh mTimer(current count time set) from move pos, array
 * 4. restart ( with noti, remain sec )
 *
 * 0814
 * repeat :
 * manage related properties : isRepeat, repeatCnt
 * manage related methods : turnRepeat, brdIsRepeat, brdRepeatCount
 *
 */

class ProcService : Service() {

    val TAG = "procService#$#"

    val REPEAT_MAX_COUNT = 99

    companion object {
        var INSTANCE: ProcService? = null // check service is alive
    }

    lateinit var timeSet: TimeSet
    lateinit var cdt: PreciseCountdown

    val timingNotification: TimingNotification by lazy {
        TimingNotification(
            this,
            ArrayList(timeSet.times.asSequence().map { it.seconds }.toList())
        )
    }

    var arrayCnt = 0
    var isRunning = false
    var isPause = false
    var mTimer = 0L // use when restart from pause status

    var isRepeat = false
    var repeatCnt = 0

    val binder = ProcServiceBinder()
    var mediaPlayer: MediaPlayer? = null

    val compositeDisposable = CompositeDisposable()

    inner class ProcServiceBinder : Binder() {
        internal val service: ProcService
            get() = this@ProcService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        isRunning = false


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        INSTANCE = this
        "onStartCommand".i()
        intent?.run {
            "action : $action".i()
            when (action) {
                CMD_SERVICE.START_WITH_TIMERS -> {
                    timeSet = intent.getParcelableExtra(ProcActivity.TIME_SET)
                    "CMD_SERVICE.START_WITH_TIMERS times : ${timeSet.times.asSequence().map { it.seconds }.toList()}.to".i()
                    restart(StartType.INIT)
                }
                CMD_SERVICE.PAUSE -> {
                    pause()
                }
                CMD_SERVICE.RESTART -> {
                    restart(StartType.RESTART)
                }
                CMD_SERVICE.STOP -> {
                    stop(true)
                }
                CMD_SERVICE.SOUND_OFF -> {
                    soundOff()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun soundOff() {

    }

    /**
     * service stop (not self stop) is only TimingActivity pass this method, broadcast
     */
    fun stop(sendStopBrdMsg: Boolean) {
        "stop , sendStopBrdMsg : $sendStopBrdMsg".i(TAG)
        cancelTimerStatus(CancleType.INIT_ARR_CNT)
        isPause = false
        stopSelf()
        INSTANCE = null

        if (sendStopBrdMsg) sendBroadcast(Intent(CMD_BRD.STOP))
    }

    fun pause() {
        "pause".i(TAG)
        cancelTimerStatus(CancleType.NONE)
        isPause = true
        updateNotification(mTimer.toTimeStr(), NotifiactionButtonType.PLAY)
    }

    fun restart(startType: StartType) {

        if (!isRunning && !isPause) timingNotification.showNotification()

        if (startType == StartType.INIT)
            mTimer = timeSet.times[0].seconds.x1000L()

        val sumSecondWithOutMTime =
            timeSet.times.asSequence().map { it.seconds }.drop(arrayCnt).reduce { cur, nxt -> cur + nxt }

        val remainSecond = mTimer / 1000 + sumSecondWithOutMTime
        sendBroadcast(Intent(CMD_BRD.REMAIN_SEC).apply { putExtra(CMD_BRD.MSG, remainSecond) })

        "restart".i(TAG)
        startTimer()
        updateNotification(mTimer.toTimeStr(), NotifiactionButtonType.PAUSE)
    }

    fun addMin() {
        pause()
        mTimer += 60000
        restart(StartType.RESTART)
    }

    fun move(pos: Int) {
        arrayCnt = pos
        pause()
        mTimer = timeSet.times[arrayCnt].seconds.x1000L() // set timer picked move
        restart(StartType.RESTART)
    }


    /**
     * startTimer is called repeat on PreciseCountdown ( recursive )
     */
    fun startTimer() {

        if (isRunning) return

        val insertTimer: Long = if (isPause) {
            isPause = false
            mTimer
        } else timeSet.times[arrayCnt].seconds.x1000L()

        cdt = object : PreciseCountdown(insertTimer, 1000L) {

            override fun onFinished() {
                isRunning = false
                arrayCnt++

                playSound()

                if (arrayCnt == timeSet.times.size) {
                    if (isRepeat && repeatCnt < REPEAT_MAX_COUNT) {
                        move(0)
                        repeatCnt++
                        brdRepeatCount()
                    } else {
                        stop(false)
//                        cancelTimerStatus(CancleType.INIT_ARR_CNT)
//                        "sendBroadcast     END".i(TAG)
                        sendBroadcast(Intent(CMD_BRD.END))
//                        stopSelf()
                    }

                } else {
                    cdt.cancel()
                    startTimer()
                    brdRound()
                }


            }

            override fun onTick(millisUntilFinished: Long) {
                mTimer = millisUntilFinished
                val time = millisUntilFinished.toTimeStr()
                "sendBroadcast     millisUntilFinished $millisUntilFinished     time $time".i(TAG)

                brdTime(time)
                updateNotification(time, NotifiactionButtonType.PAUSE)
            }
        }

        isRunning = true
        cdt.start()

    }

    fun updateNotification(time: String, nbType: NotifiactionButtonType) {
        timingNotification.update(
            "내타임셋",
            time,
            arrayCnt,
            timeSet.times.size.toString(),
            repeatCnt.toString(),
            nbType
        )
    }

    /**
     * call when rejoin activity after out activity
     */
    fun updateActViewNow() {
        brdTime(mTimer.toTimeStr())
        brdRound()
    }

    fun brdTime(timeStr: String) {
        sendBroadcast(Intent(CMD_BRD.TIME).apply { putExtra(CMD_BRD.MSG, timeStr) })
    }

    fun brdRound() {
        sendBroadcast(Intent(CMD_BRD.ROUND).apply { putExtra(CMD_BRD.MSG, arrayCnt) })
    }

    /**
     * param [initArrCnt] is called with false value from only pause()
     */
    fun cancelTimerStatus(cancleType: CancleType) {
        cdt.cancel()
        isRunning = false
        if (cancleType == CancleType.INIT_ARR_CNT) {
//            stopForeground(notiId)
            timingNotification.removeNotification()
            arrayCnt = 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stopSound() // TODO 이줄 안넣으면 compositeDisposable 이거때매 stop 안되서 무한소리, 이거넣으면 마지막에 소리안남
        compositeDisposable.clear()

        isRunning = false
//        cancelTimerStatus(CancleType.INIT_ARR_CNT)
        INSTANCE = null
    }

    fun turnRepeat() {
        isRepeat = !isRepeat
        brdIsRepeat()
    }

    fun brdIsRepeat() {
        sendBroadcast(Intent(CMD_BRD.UPDATE_REPEAT_BTN).apply { putExtra(CMD_BRD.MSG, isRepeat) })
    }

    fun brdRepeatCount() {
        sendBroadcast(Intent(CMD_BRD.UPDATE_REPEAT_CNT).apply { putExtra(CMD_BRD.MSG, repeatCnt) })
    }

    fun playSound() {
        mediaPlayer = MediaPlayer.create(this, BellManager.getBasicBells(this)[0].second)
        mediaPlayer?.start()

        compositeDisposable.add(Observable
            .timer(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                stopSound()
            }
        )
    }

    fun stopSound() {
        mediaPlayer?.stop()
    }

    enum class CancleType {
        NONE,
        INIT_ARR_CNT
    }

    enum class StartType {
        INIT,
        RESTART
    }
}
