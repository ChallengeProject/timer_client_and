package com.timer.proc

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.timer.se_data.TimeSet
import com.timer.se_util.i
import com.timer.se_util.toTimeStr
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class ProcExceedService : Service() {

    val TAG = "ProcExceedService#$#"

    companion object {
        var INSTANCE: ProcExceedService? = null // check service is alive
    }

    lateinit var timeSet: TimeSet

    val procNotification: ProcNotification by lazy {
        ProcNotification(
            this,
            ArrayList(timeSet.times.asSequence().map { it.seconds }.toList())
        )
    }

    var disposable: Disposable? = null

    var mTimer = 0L

    val binder = ProcExceedServiceBinder()

    inner class ProcExceedServiceBinder : Binder() {
        internal val service: ProcExceedService
            get() = this@ProcExceedService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
//        isRunning = false
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        INSTANCE = this
        "onStartCommand".i()
        intent?.run {
            "action : $action".i()
            when (action) {
                CMD_EXCEED_SERVICE.START_WITH_TIMERS -> {
                    timeSet = intent.getParcelableExtra(ProcActivity.TIME_SET)
                    procNotification.showNotification(NotificationUsingActivity.EXCEED_ACTIVITY)
                    "CMD_EXCEED_SERVICE.START_WITH_TIMERS times : ${timeSet.times.asSequence().map { it.seconds }.toList()}.to".i()
                    restart()
                }
                CMD_EXCEED_SERVICE.PAUSE -> {
                    pause()
                }
                CMD_EXCEED_SERVICE.RESTART -> {
                    restart()
                }
                CMD_EXCEED_SERVICE.STOP -> {
                    stop(true)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun stop(sendStopBrdMsg: Boolean) {
        "stop , sendStopBrdMsg : $sendStopBrdMsg".i(TAG)

        disposable?.dispose()
        disposable = null

        stopSelf()
        INSTANCE = null

        if (sendStopBrdMsg) sendBroadcast(Intent(CMD_BRD.STOP))
    }

    fun pause() {
        "pause".i(TAG)

        disposable?.dispose()
        disposable = null

        updateNotification((mTimer*1000).toTimeStr(), NotifiactionButtonType.PLAY)
    }


    fun restart() {
        val remainSecond = mTimer

        sendBroadcast(Intent(CMD_BRD.REMAIN_SEC).apply { putExtra(CMD_BRD.MSG, remainSecond) })

        "restart".i(TAG)
        startTimer()
//        updateNotification(mTimer.toTimeStr(), NotifiactionButtonType.PAUSE)
        updateNotification((mTimer*1000).toTimeStr(), NotifiactionButtonType.PAUSE)
    }

    fun startTimer() {

        "startTimer".i()

        disposable?.let {
            return@startTimer
        }

        disposable = Flowable
            .interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                "subscribe".i()

                mTimer++
                updateNotification((mTimer*1000).toTimeStr(), NotifiactionButtonType.PAUSE)
                brdTime((mTimer*1000).toTimeStr())
            }
    }


    fun updateNotification(time: String, nbType: NotifiactionButtonType) {
        procNotification.update(
            timeSet.title,
            time,
            ProcNotification.EXCEED_TEXT,
            ProcNotification.EXCEED_TEXT.toString(),
            nbType
        )
    }

    /**
     * call when rejoin activity after out activity
     */
    fun updateActViewNow() {
        brdTime(mTimer.toTimeStr())
    }

    fun brdTime(timeStr: String) {
        sendBroadcast(Intent(CMD_BRD.TIME).apply { putExtra(CMD_BRD.MSG, timeStr) })
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
        INSTANCE = null
    }


}
