package com.timer.proc

import android.app.Activity
import android.graphics.Point
import android.os.Build
import android.view.View
import com.timer.R
import kotlinx.android.synthetic.main.activity_proc.*

class ProcViewUpdater(val act: Activity) {

    var deviceWidth = -1

    init {
        val display = act.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        deviceWidth = size.x

//        act.rvHTRV.onTouch { v, event ->
//            hideSkipMessage()
//        }

    }

    private fun setSubtitle(visible: Int, str: String) {
        act.tvSubTitle.apply {
            visibility = visible
            text = str
        }
    }

    fun setSubtitleRepeatCnt(cnt: Int) {
        setSubtitle(View.VISIBLE, "${cnt}회 반복")
    }

    fun setSubtitleReadyCnt(cnt: Int) {
        setSubtitle(View.VISIBLE, "시작 ${cnt}초 전")
        if(cnt == 0) hideSubtitle()
    }

    fun hideSubtitle() {
        setSubtitle(View.INVISIBLE, "")
    }

    fun setTime(str: String) {
        act.tvTime.text = str
    }

    fun setTimeSetTitle(str: String) {
        act.tvTitle.text = str
    }

    fun setWholeTime(str: String) {
        act.tvTimesetTime.text = str
    }

    fun setEndTime(str: String) {
        act.tvEndTime.text = str
    }

    fun setRepeatIcon(b: Boolean) {
        act.ivRepeatBtn.setImageResource(
            if (b) R.drawable.btn_repeat
            else R.drawable.btn_recurring
        )
    }

    fun setAddTime(min: Int) {
        if(min==0) {
            hideAddTime()
            return
        }
        act.tvAddCount.visibility = View.VISIBLE
        act.tvAddCount.text = "x ${min}"
    }

    fun hideAddTime() {
        act.tvAddCount.visibility = View.INVISIBLE
        act.tvAddCount.text = ""
    }

//    fun setWriteMsgVisible(b: Boolean) {
//        act.llMemoWriteMessage.visibility =
//            if (b) View.VISIBLE
//            else View.INVISIBLE
//    }

    fun showSkipMessage(pos: Int) {
//        with(act) {
//            rvHTRV.showDownArrowAndUpdateDownArrowPos()
//            clSkipTimerMessage.visibility = View.VISIBLE
            act.clSkipMessage.visibility = View.VISIBLE
            act.ivSkipO.visibility = View.VISIBLE

            act.tvSkipMessage.text = "${pos}번쨰 타이머부터 시작하시겠어요?"

//            var rst = deviceWidth - midPos - clSkipTimerMessage.width / 2
//            if (rst + clSkipTimerMessage.width > deviceWidth) rst = deviceWidth - clSkipTimerMessage.width
//            val p = clSkipTimerMessage.layoutParams as ViewGroup.MarginLayoutParams
//            p.setMargins(0, 0, rst, 0)
//            clSkipTimerMessage.requestLayout()
//        }
    }

    // call from [ rvHTRV.onTouch listener in this class ] + alpha
    fun hideSkipMessage() {
        act.clSkipMessage.visibility = View.INVISIBLE
        act.ivSkipO.visibility = View.INVISIBLE
//        act.rvHTRV.hideDownArrow()
    }

    fun setBadgeFocus(pos :Int){
        act.rvBadges.setFocus(pos)
    }

//    fun setAlarmAndComment(alarmText: String, commentText: String) {
//        act.tvAlarm.text = alarmText
//        act.tvComment.text = commentText
//    }

//    private fun setBottomDialog(mainText: String, subText: String) {
//        with(act) {
//            rlBottomDialog.visibility = View.VISIBLE
//            act.tvBottomDialogMainText.text = mainText
//            act.tvBottomDialogSubText.text = subText
//        }
//    }

//    fun showBottomDialogTimeEndMessage(curCnt: Int, lastCnt: Int) {
//        setBottomDialog("${curCnt}번째 타이머 종료", "$curCnt/$lastCnt")
//    }

//    fun showBottomDialogRepeatEndMessage(repeatCnt: Int) {
//        setBottomDialog("반복 타임셋 종료", "${repeatCnt}회 반복")
//    }

//    fun hideBottomDialog() {
//        act.rlBottomDialog.visibility = View.INVISIBLE
//    }

    private fun setBottomButtonText(leftStr: String, rightStr: String) {
        act.btBottom1Btn.visibility = View.VISIBLE
        act.btBottom2Btn.visibility = View.VISIBLE
        act.btBottom1Btn.text = leftStr
        act.btBottom2Btn.text = rightStr
    }

    private fun setCancelAndPauseStatus() {
        setBottomButtonText("취소", "일시정지")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            act.btBottom2Btn.background = act.getDrawable(R.drawable.bg_bottom_button_gray)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            act.btBottom2Btn.setTextColor(act.getColor(R.color.se_specific_black))
        } else {
            act.btBottom2Btn.setTextColor(act.resources.getColor(R.color.se_specific_black))
        }
        setSubtitle(View.INVISIBLE,"")
    }

    private fun setCancelAndRestartStatus() {
        setBottomButtonText("취소", "시작")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            act.btBottom2Btn.background = act.getDrawable(R.drawable.bg_bottom_button_red)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            act.btBottom2Btn.setTextColor(act.getColor(R.color.se_white))
        } else {
            act.btBottom2Btn.setTextColor(act.resources.getColor(R.color.se_white))
        }
        setSubtitle(View.VISIBLE,"일시정지")
    }

    fun showBottomBtn(procStatus: ProcStatus) {
        if (procStatus == ProcStatus.ING || procStatus == ProcStatus.READY) setCancelAndPauseStatus()
        else if (procStatus == ProcStatus.PAUSE) setCancelAndRestartStatus()
    }

    fun hideBottomButton() {
        act.btBottom1Btn.visibility = View.GONE
        act.btBottom2Btn.visibility = View.GONE
    }

//    private fun showMemo() {
//        with(act) {
//            clMemoSpace.visibility = View.VISIBLE
//            etMemo.setText("")
//            tvExceedMessage.visibility = View.INVISIBLE
//            tvRemainByte.text = "0"
//        }
//    }

//    fun setMemoRemainByte(curByte: Int) {
//        act.tvRemainByte.text = curByte.toString()
//    }

//    fun hideMemo() {
//        act.clMemoSpace.visibility = View.INVISIBLE
//    }

//    fun showMemoOnly() {
//        showMemo()
//        hideBottomButton()
//    }

//    fun showMemoWithBottomBtns(procStatus: ProcStatus) {
//        showMemo()
//        showBottomBtn(procStatus)
//    }

}