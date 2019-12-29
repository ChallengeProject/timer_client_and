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
import java.util.*

class ProcActivity : AppCompatActivity() {

    val TAG = "ProcActivity"

    companion object {
        const val TIME_SET = "TIME_SET"
        const val READY_COUNT = "READY_COUNT"
        const val IS_REPEAT_ON = "IS_REPEAT_ON"
        const val TIMES_FOR_NOTIFIACTION = "TIMES_FOR_NOTIFIACTION"

        fun startProcActivity(context: Context, timeSet: TimeSet, readyCount: Int, isRepeat: Boolean) {
            (context as Activity).startActivityForResult(
                Intent(context, ProcActivity::class.java).apply {
                    putExtra(TIME_SET, timeSet)
                    putExtra(READY_COUNT, readyCount)
                    putExtra(IS_REPEAT_ON, isRepeat)
                },
                MainActivity.PROC_ACTIVITY
            )
        }

        lateinit var ymdString: String
        lateinit var startTimeString: String

        lateinit var updater: ProcViewUpdater

        lateinit var timeSet: TimeSet
        var readySec: Int = 5
        var canReady = true // exception of out when postDelay
        var timeBrd: BroadcastReceiver? = null
        lateinit var svcIntent: Intent
        var procServiceInterface: ProcServiceInterface? = null

        private var endTimeStr = ""
        private var allTimeStr = ""
        private var addingMinute = 0
        private var isRepeat = false
        private var procStatus = ProcStatus.READY

        var addMinute = 0
        var repeatCount = 0
        var pauseCount = 0
        var changeCount = 0

        lateinit var respTimeSet: TimeSet
        lateinit var respUseInfo: UseInfo
        var respEndType: Int = 0

        const val RESP_END_TYPE_NONE = 0
        const val RESP_END_TYPE_STOP = 1
        const val RESP_END_TYPE_END = 2

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc)

        updater = ProcViewUpdater(this)

        // init properties
        timeSet = intent.getParcelableExtra(TIME_SET)

        readySec = intent.getIntExtra(READY_COUNT, 5)

        isRepeat = intent.getBooleanExtra(IS_REPEAT_ON, false)

        // init view
        lsshlv.showLeftSideSnappyHorizontalListView(timeSet.times.asSequence().map { it.seconds }.toList())

        // init brd
        timeBrd = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action) {
                    CMD_BRD.TIME -> updater.setTime(intent.getStringExtra(CMD_BRD.MSG))
                    CMD_BRD.ROUND -> {
                        val round = intent.getIntExtra(CMD_BRD.MSG, 0)
                        updater.hideSkipMessage()
                        updater.setBadgeFocusAndCommentAndBell(timeSet.times[round], round)
                        updater.showBottomDialogTimeEndMessage(round, timeSet.times.size)
                    }
                    CMD_BRD.END -> {

                        timeBrd?.let {
                            unregisterReceiver(timeBrd)
                        }

                        respTimeSet = timeSet
                        respUseInfo = getCreatedUseInfo()
                        respEndType = RESP_END_TYPE_END

                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
                    }
                    CMD_BRD.STOP -> {
                        updater.setTime(timeSet.times[0].seconds.x1000L().toTimeStr())
                        ProcService.INSTANCE = null
                        endTimeStr = ""
                        allTimeStr = ""
//                        procStatus = ProcStatus.READY

                        timeBrd?.let {
                            unregisterReceiver(timeBrd)
                        }

                        val cancelInfoCount = intent.getIntExtra(ProcService.EXTRA_STOP_INFO_COUNT, 0)
                        val cancelInfoSecond = intent.getIntExtra(ProcService.EXTRA_STOP_INFO_SECOND, 0)

                        respTimeSet = timeSet
                        respUseInfo = getCreatedUseInfo().apply {
                            cancelInfo = "$cancelInfoCount#${(cancelInfoSecond / 1000).toFormattingString()}"
                        }
                        respEndType = RESP_END_TYPE_STOP

                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
                    }
                    CMD_BRD.REMAIN_SEC -> {
                        val remainSecond = intent.getLongExtra(CMD_BRD.MSG, 0)
                        "CMD_BRD.REMAIN_SEC : $remainSecond".e()
                        endTimeStr = remainSecond.toInt().toEndTimeStrAfterSec()
                        updater.setEndTime(endTimeStr)
                    }
                    CMD_BRD.UPDATE_REPEAT_BTN -> {
                        val turn = intent.getBooleanExtra(CMD_BRD.MSG, false)
                        isRepeat = turn
                        updater.setRepeatIcon(turn)
                    }
                    CMD_BRD.UPDATE_REPEAT_CNT -> {
                        repeatCount++
                        val repeatCnt = intent.getIntExtra(CMD_BRD.MSG, 0)
                        updater.setSubtitleRepeatCnt(repeatCnt)
                        updater.showBottomDialogRepeatEndMessage(repeatCnt)
                        "CMD_BRD.UPDATE_REPEAT_CNT : $repeatCnt".e()

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
                    pauseCount++
                    ProcService.INSTANCE?.let { procServiceInterface?.pause() }
                    procStatus = ProcStatus.PAUSE
                }
                ProcStatus.PAUSE -> {
                    ProcService.INSTANCE?.let { procServiceInterface?.restart() }
                    procStatus = ProcStatus.ING
                }
                ProcStatus.READY -> {
                    ToastUtil.showToast(this, "시작 준비 중에는 일시정지할 수 없어요")
                }
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
            addMinute++
            updater.setAddTime(addingMinute)
        }

        ivRepeatBtn.setOnClickListener {
            procServiceInterface?.turnRepeat(!isRepeat)
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

        lsshlv.setSelectedBadgeListener { item ->
            "onBadgeSelectedListener callback ${item.index}".e(TAG)
            updater.hideSkipMessage()
            updater.showSkipMessage(item.index)
        }

        ivSkipO.setOnClickListener {

            if (procStatus == ProcStatus.READY) {
                ToastUtil.showToast(this, "시작 준비 중에는 일시정지할 수 없어요")
                return@setOnClickListener
            }

            changeCount++

            moveTimeBadge(lsshlv.curIndex)
            updater.hideSkipMessage()

            procStatus = ProcStatus.ING
            updater.showBottomBtn(procStatus)
        }

        ivSkipX.setOnClickListener {
            updater.hideSkipMessage()
        }

        // set end time
        val allTime = timeSet.wholeTime
        if (endTimeStr.isEmpty()) endTimeStr = (readySec + allTime).toEndTimeStrAfterSec()
        if (allTimeStr.isEmpty()) allTimeStr =
            allTime.x1000L()
                .toTimeStr() // need to [if] for call from notification when remove activity status

        registerReceiver(timeBrd, IntentFilter().apply {
            addAction(CMD_BRD.ROUND)
            addAction(CMD_BRD.TIME)
            addAction(CMD_BRD.END)
            addAction(CMD_BRD.STOP)
            addAction(CMD_BRD.REMAIN_SEC)
            addAction(CMD_BRD.UPDATE_REPEAT_BTN)
            addAction(CMD_BRD.UPDATE_REPEAT_CNT)

        })
    }

    fun moveTimeBadge(pos: Int) {
        ProcService.INSTANCE?.let { procServiceInterface?.move(pos) }

        updater.setBadgeFocusAndCommentAndBell(timeSet.times[pos], pos)

        addingMinute = 0
        updater.setAddTime(addingMinute)
    }


    fun readying(cnt: Int) {

        updater.setTime(timeSet.times[0].seconds.x1000L().toTimeStr())

        procStatus = ProcStatus.READY
        updater.setSubtitleReadyCnt(cnt)

        if (!canReady) return
        if (cnt == 0) {

            addMinute = 0
            repeatCount = 0
            pauseCount = 0
            changeCount = 0


            // start service from activity
            svcIntent = Intent(this, ProcService::class.java)
                .apply {
                    putExtra(TIME_SET, timeSet)
                    action = CMD_PROC_SERVICE.START_WITH_TIMERS
                }
            startService(svcIntent)
            updater.setBadgeFocusAndCommentAndBell(timeSet.times[0], 0)

            procStatus = ProcStatus.ING
            procServiceInterface?.turnRepeat(isRepeat)

            // for showing to endProcActivity
            Calendar.getInstance().run {
                ymdString = "${get(Calendar.YEAR).toString().substring(2, 4)}. ${get(Calendar.MONTH) - 1}. ${get(Calendar.DATE)}"
            }
            startTimeString = getCurrentTimeString()

            return
        }

        Handler().postDelayed({
            readying(cnt - 1)
        }, 1000)
    }

    fun getCurrentTimeString(): String {
        Calendar.getInstance().run {
            return "${get(Calendar.HOUR)}:${get(Calendar.MINUTE)} ${if (get(Calendar.AM_PM) == 1) "PM" else "AM"}"
        }
    }

    override fun onPause() {
        super.onPause()
        canReady = false
        procServiceInterface?.unbindService()
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            timeBrd?.let {
                unregisterReceiver(timeBrd)
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()

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
            setTimeSetTitle(timeSet.title)
            setContentToHalfTransparent(false)
        }

        procServiceInterface?.getRepeat()
    }

    fun getCreatedUseInfo(): UseInfo {
        return UseInfo(
            ymdString = ymdString,
            startTimeString = startTimeString,
            endTimeString = getCurrentTimeString(),
            addMinute = addMinute,
            repeatCount = repeatCount,
            pauseCount = pauseCount,
            changeCount = changeCount
        )
    }

}
