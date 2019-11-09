package com.timer.proc

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
import com.timer.se_util.App
import com.timer.se_util.i


/**
 * LOGIC :
 *
 * 1. show notification from Service after started it
 * 2. updating views every a sec ( if running timer )
 *
 * Last. hide notification when call [cancelTimerStatus] funtion from service
 *
 */

class TimingNotification(val procService: ProcService, val times_: ArrayList<Int>) {

    val TAG = "TimingNotication"

    val NOTI_ID = 1112

    var isForeground = false

    var noti: Notification? = null

    var remoteViews: RemoteViews? = null

    var notificationManager: NotificationManager? = null

    lateinit var notifiactionButtonType: NotifiactionButtonType

    companion object {
        var times: ArrayList<Int>? =
            null // for toss previous times value to activity from notificaion
    }

    init {
        notificationManager =
            procService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun showNotification() {
        if (!isForeground) {

            times = times_

            val notificationIntent = Intent(procService, ProcActivity::class.java).apply {
                action = (Intent.ACTION_MAIN)
                addCategory(Intent.CATEGORY_LAUNCHER)
                putIntegerArrayListExtra(ProcActivity.TIMES_FOR_NOTIFIACTION, times)
            }
//            notificationIntent.action = "TIMING_NOTI_ACTION"

            // PendingIntent.FLAG_UPDATE_CURRENT :
            // Flag indicating that if the described PendingIntent already exists, then keep it
            val pendingIntent = PendingIntent.getActivity(
                procService, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            remoteViews = getRemoteView()

            val notification = NotificationCompat.Builder(procService, App.CHANNEL_ID)
                .setContentText("...")
                .setSmallIcon(R.drawable.ic_temp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCustomContentView(remoteViews)
                .build()

            isForeground = true
            procService.startForeground(NOTI_ID, notification)
            noti = notification
        }

    }

    fun removeNotification() {
        "removeNotification".i(TAG)
        if (noti != null) noti = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            procService.stopForeground(NOTI_ID)
        } else {
            procService.stop(true) // TODO check using low version device
        }
        isForeground = false
    }

    fun update(
        timeSetName: String, timer: String, step: Int, maxStep: String,
        repeat: String, notifiactionButtonType_: NotifiactionButtonType
    ) {
        "update $timeSetName $timer $step $maxStep $repeat $notifiactionButtonType_ ".i(TAG)
        notifiactionButtonType = notifiactionButtonType_
        remoteViews!!.run {

            setTextViewText(R.id.notiTvTimer, timer)

            // TODO distribute properties to each views with conditions
            setTextViewText(R.id.notiTvRepeat, "${step+1}/$maxStep")

            setViewVisibility(R.id.notiIvCtrlPlay, View.GONE)
            setViewVisibility(R.id.notiIvCtrlPause, View.GONE)

            when (notifiactionButtonType_) {
                NotifiactionButtonType.PLAY -> setViewVisibility(R.id.notiIvCtrlPlay, View.VISIBLE)
                NotifiactionButtonType.PAUSE -> setViewVisibility(
                    R.id.notiIvCtrlPause,
                    View.VISIBLE
                )
            }

        }

        notificationManager?.notify(NOTI_ID, noti)
    }

    fun getRemoteView(): RemoteViews {
        val remoteViews = RemoteViews(procService.packageName, R.layout.noti_proc)

        val playPendingIntent =
            PendingIntent.getService(procService, 0, Intent(CMD_SERVICE.RESTART), 0)
        val pausePendingIntent =
            PendingIntent.getService(procService, 0, Intent(CMD_SERVICE.PAUSE), 0)
        val stopPendingIntent =
            PendingIntent.getService(procService, 0, Intent(CMD_SERVICE.STOP), 0)

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
