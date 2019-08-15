package com.timer.timeset.proc

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.timer.R
import com.timer.util.App
import com.timer.util.i


/**
 * LOGIC :
 *
 * 1. show notification from Service after started it
 * 2. updating views every a sec ( if running timer )
 *
 * Last. hide notification when call [cancelTimerStatus] funtion from service
 *
 */

class TimingNotification(val timingService: TimingService, val times: ArrayList<Int>) {

    val TAG = "TimingNotication"

    val NOTI_ID = 111

    var isForeground = false

    var noti: Notification? = null

    var remoteViews: RemoteViews? = null

    var notificationManager: NotificationManager? = null

    lateinit var notifiactionButtonType: NotifiactionButtonType

    init {
        notificationManager = timingService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun showNotification() {
//        "showNotification".i(TAG)

        if (!isForeground) {
            val notificationIntent = Intent(timingService, TimingActivity::class.java).apply {
                putIntegerArrayListExtra(TimingActivity.TIMES, times)
            }
            notificationIntent.action = "TIMING_NOTI_ACTION"
            val pendingIntent = PendingIntent.getActivity(timingService, 0,
                notificationIntent, 0)

            remoteViews = getRemoteView()

            val notification = NotificationCompat.Builder(timingService, App.CHANNEL_ID)
                .setContentText("...")
                .setSmallIcon(R.drawable.ic_tmp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCustomContentView(remoteViews)
                .build()

            isForeground = true
            timingService.startForeground(NOTI_ID, notification)
            noti = notification
        }

    }

    fun removeNotification() {
        "removeNotification".i(TAG)
        if (noti != null) noti = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            timingService.stopForeground(NOTI_ID)
        } else {
            timingService.stop() // TODO check using low version device
        }
        isForeground = false
    }

    fun update(timeSetName: String, timer: String, step: String, maxStep: String,
               repeat: String, notifiactionButtonType_: NotifiactionButtonType) {
        "update $timeSetName $timer $step $maxStep $repeat $notifiactionButtonType_ ".i(TAG)
        notifiactionButtonType = notifiactionButtonType_
        remoteViews!!.run {

            setTextViewText(R.id.notiTvTimeSetName, timeSetName)
            setTextViewText(R.id.notiTvTimer, timer)
            setTextViewText(R.id.notiTvRepeat, "$step / $maxStep / $repeat") // TODO distribute properties to each views with conditions

            setViewVisibility(R.id.notiIvCtrlPlay, View.GONE)
            setViewVisibility(R.id.notiIvCtrlPause, View.GONE)

            when (notifiactionButtonType_) {
                NotifiactionButtonType.PLAY -> setViewVisibility(R.id.notiIvCtrlPlay, View.VISIBLE)
                NotifiactionButtonType.PAUSE -> setViewVisibility(R.id.notiIvCtrlPause, View.VISIBLE)
            }

        }

        notificationManager?.notify(NOTI_ID, noti)
    }

    fun getRemoteView(): RemoteViews {
        val remoteViews = RemoteViews(timingService.packageName, R.layout.noti_timing)

        val playPendingIntent = PendingIntent.getService(timingService, 0, Intent(CMD_SERVICE.RESTART), 0)
        val pausePendingIntent = PendingIntent.getService(timingService, 0, Intent(CMD_SERVICE.PAUSE), 0)
        val stopPendingIntent = PendingIntent.getService(timingService, 0, Intent(CMD_SERVICE.STOP), 0)

        remoteViews.setOnClickPendingIntent(R.id.notiIvCtrlPlay, playPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.notiIvCtrlPause, pausePendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.notiIvCtrlClose, stopPendingIntent)

        return remoteViews
    }

}

enum class NotifiactionButtonType {
    PLAY,
    PAUSE,
    SPEAKER,

}
