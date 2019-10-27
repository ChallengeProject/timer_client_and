package com.timer.proc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.timer.R
import com.timer.se_util.i
import com.timer.se_util.toTimeStr
import com.timer.se_util.toast
import com.timer.se_util.x1000L
import kotlinx.android.synthetic.main.activity_proc.*
import java.text.SimpleDateFormat
import java.util.*

class ProcActivity : AppCompatActivity() {

    val TAG = "ProcActivity"

    companion object {
        val TIMES = "TIMES"
        private val READY_SEC = "READY_SEC"

        fun startProcActivity(context: Context, readySec: Int, times: ArrayList<Int>) {

            context.startActivity(Intent(context, ProcActivity::class.java).apply {
                putIntegerArrayListExtra(TIMES, times)
                putExtra(READY_SEC, readySec)
            })
        }

        lateinit var updater: ProcViewUpdater

        lateinit var times: ArrayList<Int>
        var readySec: Int = 5
        var canReady = true // exception of out when postDelay
        lateinit var timeBrd: BroadcastReceiver
        lateinit var svcIntent: Intent
        var procServiceInterface: ProcServiceInterface? = null
        private val format = SimpleDateFormat("hh:mm a", Locale.US)

        // TODO : Connect sharedPreference this proerties
        private var endTimeStr = ""
        private var allTimeStr = ""
        private var addingMinute = 0
        private var isRepeat = false
        private var procStatus = ProcStatus.READY

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc)

//        rvBadges.addHomeBadge(ProcBadge(count = 0, second = k++, type = ProcBadgeType.NORMAL))
//        tvTimesetTimeText.setOnClickListener { rvBadges.setFocus(1) }

        updater = ProcViewUpdater(this)

        // init properties
        times = intent.getIntegerArrayListExtra(TIMES)
        readySec = intent.getIntExtra(READY_SEC, 5)
        "initProperties ${readySec} , ${times}".i()


        // init view
//        times.forEach {
//            rvBadges.addBadge(ProcBadge(count = -1, second = it, type = ProcBadgeType.NORMAL))
//        }
        rvBadges.addBadges(times.map {
            ProcBadge(
                count = -1,
                second = it,
                type = ProcBadgeType.NORMAL
            )
        }.toTypedArray())


        // init brd
        timeBrd = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action) {
                    CMD_BRD.TIME -> updater.setTime(intent.getStringExtra(CMD_BRD.MSG))
                    CMD_BRD.ROUND -> {
                        val round = intent.getIntExtra(CMD_BRD.MSG, 0)
//                        if (rvHTRV.getFocusPos() != round) { //
//                        updater.hideSkipMessage()
//                        rvHTRV.setFocus(round) //
                        updater.setBadgeFocus(round)
//                        updater.showBottomDialogTimeEndMessage(round, times.size)
//                        } //
                    }
                    CMD_BRD.END -> {

                        // TODO
                        tvTime.text = "종료 브로드케스팅 받음"
                        endTimeStr = ""
                        allTimeStr = ""
//                        procStatus = ProcStatus.READY
                        finish()
                    }
                    CMD_BRD.STOP -> {

                        // TODO
                        updater.setTime(times[0].x1000L().toTimeStr())
                        if (ProcService.INSTANCE != null) ProcService.INSTANCE = null
                        endTimeStr = ""
                        allTimeStr = ""
//                        procStatus = ProcStatus.READY
                        finish()
                    }
                    CMD_BRD.REMAIN_SEC -> {
                        val remainSecond = intent.getLongExtra(CMD_BRD.MSG, 0)
                        "CMD_BRD.REMAIN_SEC : $remainSecond".i()
                        endTimeStr = getEndTimeStringAfterSecond(remainSecond.toInt())
                        updater.setEndTime(endTimeStr)
                    }
                    CMD_BRD.UPDATE_REPEAT_BTN -> {
                        val turn = intent.getBooleanExtra(CMD_BRD.MSG, false)
                        isRepeat = turn
                        updater.setRepeatIcon(turn)
                    }
                    CMD_BRD.UPDATE_REPEAT_CNT -> {
                        val repeatCnt = intent.getIntExtra(CMD_BRD.MSG, 0)
                        "CMD_BRD.UPDATE_REPEAT_CNT : $repeatCnt".i()
                        "CMD_BRD.UPDATE_REPEAT_CNT : $repeatCnt".toast()

                        // TODO show and hide
                    }
                }
            }
        }

        /// initListener

        // btLeft is Cancel button always, finish app when pushed this button in ready time
        btBottom1Btn.setOnClickListener {
            if (procStatus == ProcStatus.READY) finish()
            else ProcService.INSTANCE?.let { procServiceInterface?.stop() }
        }

        // block rihgt button when pushed button in ready time
        btBottom2Btn.setOnClickListener {
            when (procStatus) {
                ProcStatus.ING -> {
                    ProcService.INSTANCE?.let { procServiceInterface?.pause() }
                    procStatus = ProcStatus.PAUSE
                }
                ProcStatus.PAUSE -> {
                    ProcService.INSTANCE?.let { procServiceInterface?.restart() }
                    procStatus = ProcStatus.ING
                }
                ProcStatus.READY -> "시작 준비에는 일시정지할 수 없습니다".toast()
            }

            updater.showBottomBtn(procStatus)

        }

        ivAddMinuteBtn.setOnClickListener {
            ProcService.INSTANCE?.let { procServiceInterface?.addMin() }
            addingMinute++
            updater.setAddTime(addingMinute)
        }

        ivRepeatBtn.setOnClickListener {
            procServiceInterface?.turnRepeat()
        }

        ivWriteMemoBtn.setOnClickListener {
            //            updater.showMemoOnly()
        }

        rvBadges.onBadgeSelectedListener = { pos ->
            "onBadgeSelectedListener callback $pos".i(TAG)
            updater.hideSkipMessage()
            updater.showSkipMessage()
        }

//        llSkipTimerMessageO.setOnClickListener {
//            moveTimeBadge(rvHTRV.getLatelyPos())
//            updater.hideSkipMessage()
//
//
//            procStatus = ProcStatus.ING
//            updater.showBottomBtn(procStatus)
//        }

//        ivSkipTimerMessageX.setOnClickListener {
//            updater.hideSkipMessage()
//        }
//
//        ivBottomDialogO.setOnClickListener {
//            updater.hideBottomDialog()
//        }


        // set end time
        val allTime = times.reduce { acc, i -> acc + i }
        if (endTimeStr.isEmpty()) endTimeStr = getEndTimeStringAfterSecond(readySec + allTime)
        if (allTimeStr.isEmpty()) allTimeStr =
            allTime.x1000L()
                .toTimeStr() // need to [if] for call from notification when remove activity status


    }

    fun moveTimeBadge(pos: Int) {
        ProcService.INSTANCE?.let { procServiceInterface?.move(pos) }
        updater.setBadgeFocus(rvBadges.getLatelyPos())
//        rvHTRV.setFocus(rvHTRV.getLatelyPos())
        addingMinute = 0
        updater.setAddTime(addingMinute)
    }


    fun readying(cnt: Int) {

        updater.setTime(times[0].x1000L().toTimeStr())

        procStatus = ProcStatus.READY
        updater.setSubtitleReadyCnt(cnt)

        if (!canReady) return
        if (cnt == 0) {

            // start service from activity
            svcIntent = Intent(this, ProcService::class.java)
                .apply {
                    putIntegerArrayListExtra(TIMES, times)
                    putExtra(READY_SEC, readySec)
                    action = CMD_SERVICE.START_WITH_TIMERS
                }
            startService(svcIntent)
            updater.setBadgeFocus(0)

            procStatus = ProcStatus.ING
            return
        }

        Handler().postDelayed({
            readying(cnt - 1)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()

        canReady = false
        unregisterReceiver(timeBrd)

        procServiceInterface?.unbindService()
    }

    fun getEndTimeStringAfterSecond(sec: Int): String {
        val gCalendar = GregorianCalendar()
        format.calendar = gCalendar
        gCalendar.add(Calendar.SECOND, sec)
        return format.format(gCalendar.time)
    }


    override fun onResume() {
        super.onResume()

        registerReceiver(timeBrd, IntentFilter().apply {
            addAction(CMD_BRD.ROUND)
            addAction(CMD_BRD.TIME)
            addAction(CMD_BRD.END)
            addAction(CMD_BRD.STOP)
            addAction(CMD_BRD.REMAIN_SEC)
            addAction(CMD_BRD.UPDATE_REPEAT_BTN)
            addAction(CMD_BRD.UPDATE_REPEAT_CNT)

        })

        canReady = true

        procServiceInterface = ProcServiceInterface(this)

//        "onResume TimingService.timingService is null : ${TimingService.timingService == null}".i()

        ProcService.INSTANCE?.let {
            Handler().postDelayed(
                { procServiceInterface?.updateActViewNow() },
                50
            ) // wait for registReceiver
        } ?: readying(readySec - 1)


        /// initView

        with(updater) {
            setWholeTime(allTimeStr)
            setEndTime(endTimeStr)
            setAddTime(addingMinute)
            setRepeatIcon(isRepeat)
            showBottomBtn(procStatus)
        }

//        updater.setBadgeFocus(0)

        procServiceInterface?.getRepeat()
    }


}
