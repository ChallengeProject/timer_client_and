package com.timer.proc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.timer.R
import com.timer.se_data.TimeSet
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
        const val TIME_SET = "TIME_SET"
        const val TIMES_FOR_NOTIFIACTION = "TIMES_FOR_NOTIFIACTION"

        fun startProcActivity(context: Context, timeSet: TimeSet) {

            context.startActivity(Intent(context, ProcActivity::class.java).apply {

                putExtra(TIME_SET, timeSet)
            })
        }

        lateinit var updater: ProcViewUpdater

        lateinit var timeSet: TimeSet
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

        updater = ProcViewUpdater(this)

        // init properties
        timeSet = intent.getParcelableExtra(TIME_SET)

        // init view
        lsshlv.showLeftSideSnappyHorizontalListView(timeSet.times.asSequence().map { it.seconds }.toList())

        // init brd
        timeBrd = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action) {
                    CMD_BRD.TIME -> updater.setTime(intent.getStringExtra(CMD_BRD.MSG))
                    CMD_BRD.ROUND -> {
                        val round = intent.getIntExtra(CMD_BRD.MSG, 0)
//                        if (rvHTRV.getFocusPos() != round) {
                        updater.hideSkipMessage()
                        updater.setBadgeFocus(round)
                        updater.showBottomDialogTimeEndMessage(round, timeSet.times.size)
//                        }
                    }
                    CMD_BRD.END -> {

                        // TODO
//                        tvTime.text = "종료 브로드케스팅 받음"
//                        endTimeStr = ""
//                        allTimeStr = ""
//                        procStatus = ProcStatus.READY

                        finish()
                        ProcEndActivity.startProcEndActivity(baseContext)
                    }
                    CMD_BRD.STOP -> {

                        // TODO
                        updater.setTime(timeSet.times[0].seconds.x1000L().toTimeStr())
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
                        updater.showBottomDialogRepeatEndMessage(repeatCnt)
                        "CMD_BRD.UPDATE_REPEAT_CNT : $repeatCnt".i()

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
                ProcStatus.READY -> "시작 준비 중에는 일시정지할 수 없습니다".toast()
            }
            updater.showBottomBtn(procStatus)
        }

        ivBottomAlarmClear.setOnClickListener {
            updater.hideBottomDialog()
            procServiceInterface?.stopSound()
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

        lsshlv.setSelectedBadgeListener { item ->
            "onBadgeSelectedListener callback ${item.index}".i(TAG)
            updater.hideSkipMessage()
            updater.showSkipMessage(item.index)
        }

        ivSkipO.setOnClickListener {

            if (procStatus == ProcStatus.READY) {
                "시작 준비 중에는 타임셋 전환을 할 수 없습니다".toast()
                return@setOnClickListener
            }

            moveTimeBadge(lsshlv.curIndex)
            updater.hideSkipMessage()

            procStatus = ProcStatus.ING
            updater.showBottomBtn(procStatus)
        }

        ivSkipX.setOnClickListener {
            updater.hideSkipMessage()
        }

        // set end time
        val allTime = timeSet.times.map { it.seconds }.reduce { acc, i -> acc + i }
        if (endTimeStr.isEmpty()) endTimeStr = getEndTimeStringAfterSecond(readySec + allTime)
        if (allTimeStr.isEmpty()) allTimeStr =
            allTime.x1000L()
                .toTimeStr() // need to [if] for call from notification when remove activity status
    }

    fun moveTimeBadge(pos: Int) {
        ProcService.INSTANCE?.let { procServiceInterface?.move(pos) }

        lsshlv.setFocus(pos)

        addingMinute = 0
        updater.setAddTime(addingMinute)
    }


    fun readying(cnt: Int) {

        updater.setTime(timeSet.times[0].seconds.x1000L().toTimeStr())

        procStatus = ProcStatus.READY
        updater.setSubtitleReadyCnt(cnt)

        if (!canReady) return
        if (cnt == 0) {

            // start service from activity
            svcIntent = Intent(this, ProcService::class.java)
                .apply {
                    putExtra(TIME_SET, timeSet)
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

        ProcService.INSTANCE?.let {
            Handler().postDelayed(
                { procServiceInterface?.updateActViewNow() },
                50
            ) // wait for registReceiver
        } ?: readying(readySec)


        /// initView
        with(updater) {
            setWholeTime(allTimeStr)
            setEndTime(endTimeStr)
            setAddTime(addingMinute)
            setRepeatIcon(isRepeat)
            showBottomBtn(procStatus)
        }

        procServiceInterface?.getRepeat()
    }


}
