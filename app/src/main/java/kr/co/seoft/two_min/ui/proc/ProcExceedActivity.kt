package kr.co.seoft.two_min.ui.proc

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_proc.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.data.UseInfo
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.*
import java.text.SimpleDateFormat
import java.util.*

class ProcExceedActivity : AppCompatActivity() {

    val TAG = "ProcExceedActivity"


    companion object {
        const val TIME_SET = "TIME_SET"
        const val TIMES_FOR_NOTIFIACTION = "TIMES_FOR_NOTIFIACTION"
        const val USE_INFO = "USE_INFO"
        const val RESP_TIME_SET = "RESP_TIME_SET"
        const val RESP_USE_INFO = "RESP_USE_INFO"

        fun startProcExceedActivity(context: Context, timeSet: TimeSet, useInfo: UseInfo) {
            (context as Activity).startActivityForResult(
                Intent(context, ProcExceedActivity::class.java).apply {
                    putExtra(TIME_SET, timeSet)
                    putExtra(USE_INFO, useInfo)
                },
                MainActivity.PROC_EXCEED_ACTIVITY
            )
        }

        lateinit var updater: ProcViewUpdater

        lateinit var timeSet: TimeSet

        var timeBrd: BroadcastReceiver? = null
        lateinit var svcIntent: Intent

        var procExceedServiceInterface: ProcExceedServiceInterface? = null

        private val format = SimpleDateFormat("hh:mm a", Locale.US)

        private var endTimeStr = ""
        private var allTimeStr = ""
        private var procStatus = ProcStatus.READY
        private var readyCount = 5
        private lateinit var useInfo: UseInfo

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc)

        updater = ProcViewUpdater(this)
        timeSet = intent.getParcelableExtra(TIME_SET)
        useInfo = intent.getParcelableExtra(USE_INFO)

        lsshlv.showLeftSideSnappyHorizontalListView(timeSet.times.asSequence().map { it.seconds }.toList())

        timeBrd = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action) {
                    CMD_BRD.TIME -> {
                        "CMD_BRD.TIME".e()
                        intent.getStringExtra(CMD_BRD.MSG).e()
                        updater.setTime(intent.getStringExtra(CMD_BRD.MSG))
                    }
                    CMD_BRD.STOP -> {

                        updater.setTime(timeSet.times[0].seconds.x1000L().toTimeStr())
                        ProcExceedService.INSTANCE = null
                        endTimeStr = ""
                        allTimeStr = ""
                        procStatus = ProcStatus.READY

                        timeBrd?.let {
                            unregisterReceiver(timeBrd)
                        }

                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra(RESP_TIME_SET, timeSet)
                            putExtra(RESP_USE_INFO, useInfo.apply {
                                exceedSecond = timeSet.times[0].seconds
                            })
                        })

                        finish()
                    }
                }
            }
        }

        // btLeft is Cancel button always, finish app when pushed this button in ready time
        btBottom1Btn.setOnClickListener {
            ProcExceedService.INSTANCE?.let { procExceedServiceInterface?.stop() }
        }

        // block rihgt button when pushed button in ready time
        btBottom2Btn.setOnClickListener {
            when (procStatus) {
                ProcStatus.ING -> {
                    ProcExceedService.INSTANCE?.let { procExceedServiceInterface?.pause() }
                    procStatus = ProcStatus.PAUSE
                }
                ProcStatus.PAUSE -> {
                    ProcExceedService.INSTANCE?.let { procExceedServiceInterface?.restart() }
                    procStatus = ProcStatus.ING
                }
            }
            updater.showBottomBtn(procStatus,false)
        }

        ivWriteMemoBtn.setOnClickListener {
            updater.showMemo(Preferencer.getCurrentMemo(this))
        }

        ivCloseMemoBtn.setOnClickListener {
            Preferencer.setCurrentMemo(this, etMemo.text.toString())
            updater.hideMemo()
        }

        EditTextLengthExceedCheckUtil.checkAndBlockExceed(
            etMemo,
            tvExceedNumber,
            1000,
            R.color.ux_black,
            R.color.ux_pink
        )

        val allTime = timeSet.wholeTime
        if (endTimeStr.isEmpty()) endTimeStr = (readyCount + allTime).toEndTimeStrAfterSec()
        if (allTimeStr.isEmpty()) allTimeStr =
            allTime.x1000L()
                .toTimeStr() // need to [if] for call from notification when remove activity status

        svcIntent = Intent(this, ProcExceedService::class.java)
            .apply {
                putExtra(TIME_SET, timeSet)
                action = CMD_EXCEED_SERVICE.START_WITH_TIMERS
            }
        startService(svcIntent)
        updater.setBadgeFocusAndCommentAndBell(timeSet.times[0], 0)

        procStatus = ProcStatus.ING

        registerReceiver(timeBrd, IntentFilter().apply {
            addAction(CMD_BRD.TIME)
            addAction(CMD_BRD.STOP)
            addAction(CMD_BRD.REMAIN_SEC)
        })
    }

    override fun onPause() {
        super.onPause()
        procExceedServiceInterface?.unbindService()
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            ProcActivity.timeBrd?.let {
                unregisterReceiver(ProcActivity.timeBrd)
            }
        } catch (e: Exception) {
        }

    }

    override fun onResume() {
        super.onResume()

        with(updater) {
            setWholeTime(allTimeStr)
            setEndTime(endTimeStr)
            showBottomBtn(procStatus,false)
        }

        procExceedServiceInterface = ProcExceedServiceInterface(this)

        Handler().postDelayed(
            { procExceedServiceInterface?.updateActViewNow() },
            50
        ) // wait for registReceiver

        /// initView
        with(updater) {
            setWholeTime(allTimeStr)
            setEndTime(endTimeStr)
            setRepeatIcon(false)
            showBottomBtn(procStatus,false)
            setTimeSetTitle(timeSet.title)
            setTimeColor(R.color.ux_pink.color())
            setContentToHalfTransparent(true)
        }

    }


}
