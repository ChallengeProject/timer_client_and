package kr.co.seoft.two_min.ui.history

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import gun0912.tedkeyboardobserver.TedKeyboardObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.History
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.history.HistoriesActivity.Companion.HISTORY_ACTIVITY
import kr.co.seoft.two_min.util.*


class HistoryActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_history

    companion object {
        private const val TAG = "HistoryActivity"

        private const val EXTRA_HISTORY_ID = "EXTRA_HISTORY_ID"
        const val RESP_HISTORY_RESPONSE_TYPE = "RESP_HISTORY_RESPONSE_TYPE"
        const val RESP_TIME_SET = "RESP_TIME_SET"

        const val HISTORY_RESP_TYPE_SAVE = 1
        const val HISTORY_RESP_TYPE_START = 2


        fun startHistoryActivity(context: Context, historyId: Long) {
            (context as Activity).startActivityForResult(
                Intent(context, HistoryActivity::class.java).apply {
                    putExtra(EXTRA_HISTORY_ID, historyId)
                }, HISTORY_ACTIVITY
            )
        }
    }

    val db by lazy { AppDatabase.getDatabase(this) }

    lateinit var timeSet: TimeSet

    private val compositeDisposable = CompositeDisposable()

    val historyId by lazy {
        intent.getLongExtra(EXTRA_HISTORY_ID, -1L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initListener()
        loadDataFromDatabase()

    }

    fun loadDataFromDatabase() {
        compositeDisposable.add(
            db.timeSetDao()
                .getHistoryByHistoryId(historyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setHistoryView(it)
                    loadTimeSet(it.timeSetId)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun loadTimeSet(timeSetId: Long) {
        compositeDisposable.add(
            db.timeSetDao()
                .getTimeSetById(timeSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setTimeSetView(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun setHistoryView(history: History) {

        actHistoryTvAddTime.text = "+${history.addMinute}분"
        actHistoryTvUseBoundary.text = "${history.ymdString} / ${history.startTimeString} - ${history.endTimeString}"
        actHistoryTvTitle.text = if (history.timeSetTitle.isEmpty()) "무제 타임셋" else history.timeSetTitle
        actHistoryTvRepeatCount.text = "${history.repeatCount}회"
        actHistoryTvTimeSetTime.text = history.wholeTime.toFormattingString()

        var etcString = ""

        if (history.pauseCount != 0) etcString += "일시정지 ${history.pauseCount}회,"
        if (history.changeCount != 0) etcString += "진행 중 타이머 변경 ${history.changeCount}회"

        actHistoryTvEtcInfo.text = if (etcString.isEmpty()) "없음" else etcString

        if (history.cancelInfo.isNotEmpty()) {
            actHistoryClBubbleLayout.visibility = View.VISIBLE
            actHistoryTvBubbleMessage.text = "${history.cancelInfo.split("#")[0]}번째 타이머에서" +
                    "\n${history.cancelInfo.split("#")[1]} 남기고 취소"
        }

        if (history.exceedSecond != 0) {

            var exceedString = "${history.exceedSecond.toFormattingString()}\n초과 기록"

            val ssb = SpannableStringBuilder(exceedString)
            ssb.setSpan(
                ForegroundColorSpan(
                    R.color.ux_pink.color()
                ),
                exceedString.length - 5,
                exceedString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            actHistoryClBubbleLayout.visibility = View.VISIBLE
            actHistoryTvBubbleMessage.text = ssb
        }
    }

    fun setTimeSetView(timeSet_: TimeSet) {

        timeSet = timeSet_

        actHistoryEtMemo.text = timeSet.memo.toEditable()
        actHistoryTvExceedNumber.text = timeSet.memo.length.toString()
        actHistoryLsshlv.showLeftSideSnappyHorizontalListView(timeSet.times.map { it.seconds }.toList())
    }

    fun initListener() {
        actHistoryBtBottom1Btn.setOnClickListener {
            setResult(Activity.RESULT_OK,
                Intent().apply {
                    putExtra(RESP_HISTORY_RESPONSE_TYPE, HISTORY_RESP_TYPE_SAVE)
                    putExtra(RESP_TIME_SET, timeSet)
                })
            finish()
        }

        actHistoryBtBottom2Btn.setOnClickListener {
            setResult(Activity.RESULT_OK,
                Intent().apply {
                    putExtra(RESP_HISTORY_RESPONSE_TYPE, HISTORY_RESP_TYPE_START)
                    putExtra(RESP_TIME_SET, timeSet)
                })

            finish()
        }

        actHistoryIvBubbleClose.setOnClickListener {
            actHistoryClBubbleLayout.visibility = View.INVISIBLE
        }


        actHistoryEtMemo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s!!.length > 1000) {
                    actHistoryEtMemo.setText(s.substring(0, s.length - 2))
                }

                actHistoryTvExceedNumber.text = s.length.toString()
                if (s.length > 999) {
                    actHistoryTvExceedNumber.setTextColor(R.color.ux_pink.color())
                    actHistoryTvExceedErrorText.visibility = View.VISIBLE
                    return
                }
                actHistoryTvExceedErrorText.visibility = View.INVISIBLE
                actHistoryTvExceedNumber.setTextColor(R.color.ux_black.color())
            }
        })


        TedKeyboardObserver(this).listen {
            actHistoryClContent.visibility = (!it).setVisibleOrGone()
//            actHistoryLsshlv.visibility = (!it).setVisibleOrGone()
            actHistoryLlBottomButtons.visibility = (!it).setVisibleOrGone()
        }

    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.___ic_back)
            setTitle("히스토리 상세")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
