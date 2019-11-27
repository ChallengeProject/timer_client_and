package kr.co.seoft.two_min.ui.proc

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_proc.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.*
import java.text.SimpleDateFormat
import java.util.*

class ProcExceedActivity : AppCompatActivity() {

    val TAG = "ProcExceedActivity"


    companion object {
        const val TIME_SET = "TIME_SET"
        const val TIMES_FOR_NOTIFIACTION = "TIMES_FOR_NOTIFIACTION"
        const val RESP_TIME_SET = "RESP_TIME_SET"

        fun startProcExceedActivity(context: Context, timeSet: TimeSet) {
            (context as Activity).startActivityForResult(
                Intent(context, ProcExceedActivity::class.java).apply {
                    putExtra(TIME_SET, timeSet)
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc)

        updater = ProcViewUpdater(this)
        timeSet = intent.getParcelableExtra(TIME_SET)

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
            updater.showBottomBtn(procStatus)
        }

        ivWriteMemoBtn.setOnClickListener {
            updater.showMemo(Preferencer.getCurrentMemo(this))
        }

        ivCloseMemoBtn.setOnClickListener {
            Preferencer.setCurrentMemo(this, etMemo.text.toString())
            updater.hideMemo()
        }

        etMemo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length > 1000) {
                    etMemo.setText(s!!.substring(999,s!!.length-1))
                }
                updater.setMemoRemainByte(s!!.length)
            }
        })

        val allTime = timeSet.times.map { it.seconds }.reduce { acc, i -> acc + i }
        if (endTimeStr.isEmpty()) endTimeStr = (timeSet.readySecond + allTime).toEndTimeStrAfterSec()
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
            showBottomBtn(procStatus)
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
            showBottomBtn(procStatus)
            setTimeSetTitle(timeSet.title)
            setTimeColor(Color.parseColor("#f24150")) // 0xfff24150
            setContentToHalfTransparent(true)
        }

    }


}
