package com.timer.timeset.proc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.timer.R
import com.timer.util.*
import kotlinx.android.synthetic.main.fragment_proc_timeset.*
import java.text.SimpleDateFormat
import java.util.*

class TimingActivity : AppCompatActivity() {

    val TAG = "TimingActivity"

    companion object {
        val TIMES = "TIMES"
        val READY_SEC = "READY_SEC"

        // TODO : Connect sharedPreference this proerties
        private var endTimeStr = ""
        private var allTimeStr = ""
        private var addingMinute = 0
        private var isRepeat = false
        private var procStatus = ProcStatus.READY
    }

    lateinit var updater: ProcViewUpdater

    lateinit var times: ArrayList<Int>
    var readySec: Int = 0
    var canReady = true // exception of out when postDelay
    lateinit var timeBrd: BroadcastReceiver
    lateinit var svcIntent: Intent
    var timingServiceInterface: TimingServiceInterface? = null
    private val format = SimpleDateFormat("hh:mm a", Locale.US)
//    var latelyPos = 0 // set in onBadgeSelectedListener and use from moveTimeBadge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_proc_timeset)

        updater = ProcViewUpdater(this)

        initProperties()

        initViews()
        initListener()
        initBrd()

        // set end time
        val allTime =  times.reduce { acc, i ->  acc+i }
        if(endTimeStr.isEmpty()) endTimeStr = getEndTimeStringAfterSecond(readySec + allTime)
        if(allTimeStr.isEmpty()) allTimeStr = allTime.x1000L().toTimeStr() // need to [if] for call from notification when remove activity status

    }

    fun initProperties() {
        times = intent.getIntegerArrayListExtra(TIMES)

        "initProperties ${times}".i()
        readySec = intent.getIntExtra(READY_SEC, 5)

    }

    fun initViews() {

        times.forEach {
            rvHTRV.addTimesetBadge(TimesetBadge(second = it, type = TimesetBadgeType.NORMAL))
        }

//        tvTopNotification.text = "시작 5초 전"

        tvTime.text = "00:00:00"
        tvTimeSetName.text = "타임셋명"

        tvEndTime.text = "00:00 AM"
        tvWholeTime.text = "00시간 00분 00초"

        tvAlarm.text = "기본음"
        tvComment.text = "코맨트"
    }

    fun initBrd(){
        timeBrd = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action) {
                    CMD_BRD.TIME -> updater.setTime( intent.getStringExtra(CMD_BRD.MSG) )
                    CMD_BRD.ROUND -> {
                        val round = intent.getIntExtra(CMD_BRD.MSG,0)
                        if(rvHTRV.getFocusPos() != round) {
                            updater.hideSkipMessage()
                            rvHTRV.setFocus(round)
                            updater.showBottomDialogTimeEndMessage(round,times.size)
                        }
                    }
                    CMD_BRD.END -> {

                        // TODO
                        tvTime.text = "종료 브로드케스팅 받음"
                        endTimeStr = ""
                        allTimeStr = ""
                        finish()
                    }
                    CMD_BRD.STOP -> {

                        // TODO
                        tvTime.text = times[0].x1000L().toTimeStr()
                        if(TimingService.timingService != null) TimingService.timingService = null
                        endTimeStr = ""
                        allTimeStr = ""
                        finish()
                    }
                    CMD_BRD.REMAIN_SEC -> {
                        val remainSecond = intent.getLongExtra(CMD_BRD.MSG,0)
                        "CMD_BRD.REMAIN_SEC : $remainSecond".i()
                        endTimeStr = getEndTimeStringAfterSecond(remainSecond.toInt())
                        updater.setEndTime(endTimeStr)
                    }
                    CMD_BRD.UPDATE_REPEAT_BTN -> {
                        val turn = intent.getBooleanExtra(CMD_BRD.MSG,false)
                        isRepeat = turn
                        updater.setRepeatIcon(turn)
                    }
                    CMD_BRD.UPDATE_REPEAT_CNT -> {
                        val repeatCnt = intent.getIntExtra(CMD_BRD.MSG,0)
                        "CMD_BRD.UPDATE_REPEAT_CNT : $repeatCnt".i()
                        "CMD_BRD.UPDATE_REPEAT_CNT : $repeatCnt".toast()

                        // TODO show and hide
                    }
                }
            }
        }
    }

    fun initListener() {

        // TODO : block push button before reading
        btRight.setOnClickListener {
            when(procStatus) {
                ProcStatus.READY, ProcStatus.ING -> {
                    TimingService.timingService?.let { timingServiceInterface?.pause() }
                    procStatus = ProcStatus.PAUSE
                }
                ProcStatus.PAUSE -> {
                    TimingService.timingService?.let { timingServiceInterface?.restart() }
                    procStatus = ProcStatus.ING
                }
            }

            updater.showBottomBtn(procStatus)

        }

        // btLeft is Cancel button always
        btLeft.setOnClickListener {
            TimingService.timingService?.let { timingServiceInterface?.stop() }
        }

        tvAddTime_.setOnClickListener {
            TimingService.timingService?.let { timingServiceInterface?.addMin() }
            addingMinute++
            updater.setAddTime(addingMinute)
        }

        ivRepeat.setOnClickListener {
            timingServiceInterface?.turnRepeat()
        }

        tvMemo.setOnClickListener {
            updater.showMemoOnly()
        }

        rvHTRV.onBadgeSelectedListener = { pos ->
            "onBadgeSelectedListener callback $pos".i(TAG)
            updater.hideSkipMessage()
            updater.showSkipMessage(rvHTRV.latelyMidPosX)
        }

        llSkipTimerMessageO.setOnClickListener{
            moveTimeBadge(rvHTRV.getLatelyPos())
            updater.hideSkipMessage()

            procStatus = ProcStatus.ING
            updater.showBottomBtn(procStatus)
        }

        ivSkipTimerMessageX.setOnClickListener {
            updater.hideSkipMessage()
        }

        ivBottomDialogO.setOnClickListener {
            updater.hideBottomDialog()
        }




    }

    fun moveTimeBadge(pos:Int){
        TimingService.timingService?.let { timingServiceInterface?.move(pos)}
        addingMinute=0
        updater.setAddTime(addingMinute)
    }

    fun readying(cnt: Int) {

        procStatus = ProcStatus.READY
        updater.setTopNotiReadyCnt(cnt)

        if (!canReady) return
        if (cnt == 0) {

            // start service from activity
            svcIntent = Intent(this, TimingService::class.java)
                .apply {
                    putIntegerArrayListExtra(TIMES, times)
                    putExtra(READY_SEC, readySec)
                    action = CMD_SERVICE.START_WITH_TIMERS
                }
            startService(svcIntent)

            procStatus = ProcStatus.ING
            return
        }

        Handler().postDelayed({
            readying(cnt - 1)
        }, 1000)
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

        timingServiceInterface = TimingServiceInterface(this)

//        "onResume TimingService.timingService is null : ${TimingService.timingService == null}".i()

        TimingService.timingService?.let {
            Handler().postDelayed({ timingServiceInterface?.updateActViewNow() }, 50) // wait for registReceiver
        } ?: readying(readySec - 1)


        // init view

        with(updater){
            setWholeTime(allTimeStr)
            setEndTime(endTimeStr)
            setAddTime(addingMinute)
            showBottomBtn(procStatus)
            setRepeatIcon(isRepeat)
        }



        timingServiceInterface?.getRepeat()
    }

    override fun onPause() {
        super.onPause()

        canReady = false
        unregisterReceiver(timeBrd)

        timingServiceInterface?.unbindService()
    }


    fun getEndTimeStringAfterSecond(sec:Int) :String {
        val gCalendar = GregorianCalendar()
        format.calendar = gCalendar
        gCalendar.add(Calendar.SECOND,sec)
        return format.format(gCalendar.time)
    }


}