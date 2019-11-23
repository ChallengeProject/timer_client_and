package com.timer.proc

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.timer.R
import com.timer.se_data.TimeSet
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

class ProcNotification(val service: Service, val times_: ArrayList<Int>) {

    val TAG = "TimingNotication"

    val NOTI_ID = 1112

    var isForeground = false

    var noti: Notification? = null

    var remoteViews: RemoteViews? = null

    var notificationManager: NotificationManager? = null

    lateinit var notifiactionButtonType: NotifiactionButtonType

    lateinit var notificationUsingActivity: NotificationUsingActivity

    companion object {
//        var times: ArrayList<Int>? =
//            null // for toss previous times value to activity from notificaion

        lateinit var timeSet: TimeSet

        const val EXCEED_TEXT = -1

    }

    init {
        notificationManager =
            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun isProcAct(): Boolean {
        return notificationUsingActivity == NotificationUsingActivity.PROC_ACTIVITY
    }

    fun showNotification(notificationUsingActivity_: NotificationUsingActivity, timeSet_: TimeSet) {

        notificationUsingActivity = notificationUsingActivity_

        if (!isForeground) {

//            times = times_
            timeSet = timeSet_

            val clazz =
                if (isProcAct()) ProcActivity::class.java
                else ProcExceedActivity::class.java

            val extraKey =
                if (isProcAct()) ProcActivity.TIMES_FOR_NOTIFIACTION
                else ProcExceedActivity.TIMES_FOR_NOTIFIACTION

            val notificationIntent = Intent(service, clazz).apply {
                action = (Intent.ACTION_MAIN)
                addCategory(Intent.CATEGORY_LAUNCHER)
                putIntegerArrayListExtra(extraKey, ArrayList(timeSet.times.map { it.seconds }.toList()))
                putExtra(ProcActivity.TIME_SET, timeSet)
            }

//            val notificationIntent: Intent =
//                if (notificationUsingActivity == NotificationUsingActivity.PROC_ACTIVITY) {
//
//                    Intent(service, ProcActivity::class.java).apply {
//                        action = (Intent.ACTION_MAIN)
//                        addCategory(Intent.CATEGORY_LAUNCHER)
//                        putIntegerArrayListExtra(ProcActivity.TIMES_FOR_NOTIFIACTION, ArrayList(timeSet.times.map { it.seconds }.toList()))
//                    }
//                } else { //if (notificationUsingActivity == NotificationUsingActivity.EXCEED_ACTIVITY) {
//
//                    Intent(service, ProcExceedActivity::class.java).apply {
//                        action = (Intent.ACTION_MAIN)
//                        addCategory(Intent.CATEGORY_LAUNCHER)
//                        putIntegerArrayListExtra(ProcExceedActivity.TIMES_FOR_NOTIFIACTION, ArrayList(timeSet.times.map { it.seconds }.toList()))
//                    }
//                }

//            notificationIntent.action = "TIMING_NOTI_ACTION"

            // PendingIntent.FLAG_UPDATE_CURRENT :
            // Flag indicating that if the described PendingIntent already exists, then keep it
            val pendingIntent = PendingIntent.getActivity(
                service, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            remoteViews = getRemoteView()

            val notification = NotificationCompat.Builder(service, App.CHANNEL_ID)
                .setContentText("...")
                .setSmallIcon(R.drawable.ic_temp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCustomContentView(remoteViews)
                .build()

            isForeground = true
            service.startForeground(NOTI_ID, notification)
            noti = notification
        }

    }

    fun removeNotification() {
        "removeNotification".i(TAG)
        if (noti != null) noti = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            service.stopForeground(NOTI_ID)
        } else {

            if (isProcAct()) {
                (service as ProcService).stop(true)
            } else { //if (notificationUsingActivity == NotificationUsingActivity.EXCEED_ACTIVITY) {
                (service as ProcExceedService).stop(true)
            }

        }
        isForeground = false
    }

    fun update(
        timeSetName: String, timer: String, step: Int, maxStep: String,
        notifiactionButtonType_: NotifiactionButtonType
    ) {
        "update $timeSetName $timer $step $maxStep $notifiactionButtonType_ ".i(TAG)
        notifiactionButtonType = notifiactionButtonType_
        remoteViews!!.run {

            setTextViewText(R.id.notiTvTimer, timer)

            if (step == EXCEED_TEXT && maxStep == EXCEED_TEXT.toString()) {
                setTextViewText(R.id.notiTvRepeat, "초과 기록")
                setTextColor(R.id.notiTvRepeat, ContextCompat.getColor(service.baseContext, R.color.ux_pink))
            } else {
                setTextViewText(R.id.notiTvRepeat, "${step + 1}/$maxStep")
            }

            setViewVisibility(R.id.notiIvCtrlPlay, View.GONE)
            setViewVisibility(R.id.notiIvCtrlPause, View.GONE)

            when (notifiactionButtonType) {
                NotifiactionButtonType.PLAY -> setViewVisibility(R.id.notiIvCtrlPlay, View.VISIBLE)
                NotifiactionButtonType.PAUSE -> setViewVisibility(R.id.notiIvCtrlPause, View.VISIBLE)
            }

        }

        notificationManager?.notify(NOTI_ID, noti)
    }

    fun getRemoteView(): RemoteViews {
        val remoteViews = RemoteViews(service.packageName, R.layout.noti_proc)

        val playPendingIntent =
            if (isProcAct()) PendingIntent.getService(service, 0, Intent(CMD_PROC_SERVICE.RESTART), 0)
            else PendingIntent.getService(service, 0, Intent(CMD_EXCEED_SERVICE.RESTART), 0)
        val pausePendingIntent =
            if (isProcAct()) PendingIntent.getService(service, 0, Intent(CMD_PROC_SERVICE.PAUSE), 0)
            else PendingIntent.getService(service, 0, Intent(CMD_EXCEED_SERVICE.PAUSE), 0)

        val stopPendingIntent =
            if (isProcAct()) PendingIntent.getService(service, 0, Intent(CMD_PROC_SERVICE.STOP), 0)
            else PendingIntent.getService(service, 0, Intent(CMD_EXCEED_SERVICE.STOP), 0)


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

enum class NotificationUsingActivity {
    PROC_ACTIVITY,
    EXCEED_ACTIVITY
}
