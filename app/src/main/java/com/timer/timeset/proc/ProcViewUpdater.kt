package com.timer.timeset.proc

import android.app.Activity
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import com.timer.R
import kotlinx.android.synthetic.main.fragment_proc_timeset.*
import org.jetbrains.anko.sdk27.coroutines.onTouch

class ProcViewUpdater(val act: Activity) {

    var deviceWidth = -1

    init {
        val display = act.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        deviceWidth = size.x

        act.rvHTRV.onTouch { v, event ->
            hideSkipMessage()
        }

    }

    private fun setTopNoti(visible: Int, str: String) {
        act.tvTopNotification.visibility = visible
        act.tvTopNotification.text = str
    }

    fun setTopNotiRepeatCnt(cnt: Int) {
        setTopNoti(View.VISIBLE, "${cnt}회 반복")
    }

    fun setTopNotiReadyCnt(cnt: Int) {
        setTopNoti(View.VISIBLE, "${cnt}회 반복")
    }

    fun hideTopNoti() {
        setTopNoti(View.INVISIBLE, "")
    }

    fun setTime(str: String) {
        act.tvTimeSetName.text = str
    }

    fun setTimeSetName(str: String) {
        act.tvTimeSetName.text = str
    }

    fun setWholeTime(str: String) {
        act.tvWholeTime.text = str
    }

    fun setEndTime(str: String) {
        act.tvEndTime.text = str
    }

    fun setRepeatIcon(b: Boolean) {
        act.ivRepeat.setImageResource(
            if (b) R.drawable.btn_repeat
            else R.drawable.btn_recurring
        )
    }

    fun setAddTime(min: Int) {
        act.tvAddTime.visibility = View.VISIBLE
        act.tvAddTime.text = "+${min}분"
    }

    fun hideAddTime() {
        act.tvAddTime.visibility = View.INVISIBLE
        act.tvAddTime.text = ""
    }

    fun setWriteMsgVisible(b: Boolean) {
        act.llMemoWriteMessage.visibility =
            if (b) View.VISIBLE
            else View.INVISIBLE
    }

    fun showSkipMessage(midPos: Int) {
        with(act) {
            rvHTRV.showDownArrowAndUpdateDownArrowPos()
            clSkipTimerMessage.visibility = View.VISIBLE
            var rst = deviceWidth - midPos - clSkipTimerMessage.width / 2
            if (rst + clSkipTimerMessage.width > deviceWidth) rst = deviceWidth - clSkipTimerMessage.width
            val p = clSkipTimerMessage.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(0, 0, rst, 0)
            clSkipTimerMessage.requestLayout()
        }
    }

    // call from [ rvHTRV.onTouch listener , onBadgeSelectedListener ]
    fun hideSkipMessage() {
        act.clSkipTimerMessage.visibility = View.INVISIBLE
        act.rvHTRV.hideDownArrow()
    }

    fun setAlarmAndComment(alarmText: String, commentText: String) {
        act.tvAlarm.text = alarmText
        act.tvComment.text = commentText
    }

    private fun setBottomDialog(mainText: String, subText: String) {
        with(act) {
            rlBottomDialog.visibility = View.VISIBLE
            act.tvBottomDialogMainText.text = mainText
            act.tvBottomDialogSubText.text = subText
        }
    }

    fun showBottomDialogTimeEndMessage(curCnt: Int, lastCnt: Int) {
        setBottomDialog("${curCnt}번째 타이머 종료", "$curCnt/$lastCnt")
    }

    fun showBottomDialogRepeatEndMessage(repeatCnt: Int) {
        setBottomDialog("반복 타임셋 종료", "${repeatCnt}회 반복")
    }

    fun hideBottomDialog() {
        act.rlBottomDialog.visibility = View.INVISIBLE
    }

    private fun setBottomButton(leftStr: String, rightStr: String) {
        act.llBottomButtion.visibility = View.VISIBLE
        act.btLeft.text = leftStr
        act.btRight.text = rightStr
    }

    private fun setBottomBtnCancelAndPause() {
        setBottomButton("취소", "일시정지")
    }

    private fun setBottomBtnCancelAndRestart() {
        setBottomButton("취소", "재시작")
    }

    fun showBottomBtn(procStatus: ProcStatus) {
        if (procStatus == ProcStatus.ING || procStatus == ProcStatus.READY) setBottomBtnCancelAndPause()
        else if (procStatus == ProcStatus.PAUSE) setBottomBtnCancelAndRestart()
    }

    fun hideBottomButton() {
        act.llBottomButtion.visibility = View.GONE
    }

    private fun showMemo() {
        with(act) {
            clMemoSpace.visibility = View.VISIBLE
            etMemo.setText("")
            tvExceedMessage.visibility = View.INVISIBLE
            tvRemainByte.text = "0"
        }
    }

    fun setMemoRemainByte(curByte: Int) {
        act.tvRemainByte.text = curByte.toString()
    }

    fun hideMemo() {
        act.clMemoSpace.visibility = View.INVISIBLE
    }

    fun showOnlyMemo() {
        showMemo()
        hideBottomButton()
    }

    fun showMemoWithBottomBtns(procStatus: ProcStatus) {
        showMemo()
        showBottomBtn(procStatus)
    }

}