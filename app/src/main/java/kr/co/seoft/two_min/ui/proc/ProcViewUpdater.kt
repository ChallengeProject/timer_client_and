package kr.co.seoft.two_min.ui.proc

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_proc.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.data.Time
import kr.co.seoft.two_min.util.color
import kr.co.seoft.two_min.util.en


class ProcViewUpdater(val act: Activity) {

    init {
        if (en()) {
            act.ivAddMinuteBtn.setImageResource(R.drawable._bt_add_minute_en)
            act.ivWriteMemoBtn.setImageResource(R.drawable._bt_input_memo_en)
        }
    }

    private fun setSubtitle(visible: Int, str: String) {
        act.tvSubTitle.apply {
            visibility = visible
            text = str
        }
        act.viewSubTitleRedDot.visibility = visible
    }

    fun setSubtitleRepeatCnt(cnt: Int) {
        setSubtitle(View.VISIBLE, "${cnt}${act.getString(R.string.ghl_repeat)}")
    }

    fun setSubtitleReadyCnt(cnt: Int) {
        if (en()) {
            setSubtitle(View.VISIBLE, "${cnt} Seconds")
        } else {
            setSubtitle(View.VISIBLE, "시작 ${cnt}초 전")
        }
        if (cnt == 0) hideSubtitle()
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

    fun setTimeColor(color: Int) {
        act.tvTime.setTextColor(color)
    }

    fun setWholeTime(str: String) {
        act.tvTimesetTime.text = str
    }

    fun setEndTime(str: String) {
        act.tvEndTime.text = str
    }

    fun setRepeatIcon(b: Boolean) {
        act.ivRepeatBtn.setImageResource(
            if (b) R.drawable._bt_repeat
            else R.drawable._bt_non_repeat
        )
    }

    fun setAddTime(min: Int) {
        if (min == 0) {
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
        act.clSkipMessage.visibility = View.VISIBLE
        act.ivSkipO.visibility = View.VISIBLE

        if (en()) {
            act.tvSkipMessage.text = "Strart with the ${pos + 1} timer."
        } else {
            act.tvSkipMessage.text = "${pos + 1}번쨰 타이머부터 시작하시겠어요?"
        }
    }

    // call from [ rvHTRV.onTouch listener in this class ] + alpha
    fun hideSkipMessage() {
        act.clSkipMessage.visibility = View.INVISIBLE
        act.ivSkipO.visibility = View.INVISIBLE
    }

    fun setBadgeFocus(pos: Int) {
        act.lsshlv.setFocus(pos)
    }

    fun setComment(comment: String) {
        act.tvComment.text = comment
    }

    fun setBadgeFocusAndCommentAndBell(time: Time, round: Int) {
        setBadgeFocus(round)
        setComment(time.comment)
        setSound(time.bell)
    }

    fun setSound(bell: Bell) {

        if (en()) {
            act.tvSound.text = when (bell.type) {
                Bell.Type.SLIENT -> "Mute"
                Bell.Type.VIBRATION -> "Vibration"
                Bell.Type.DEFAULT -> "Default"
                Bell.Type.USER -> "사용자 지정음"
            }
        } else {
            act.tvSound.text = when (bell.type) {
                Bell.Type.SLIENT -> "무음"
                Bell.Type.VIBRATION -> "진동"
                Bell.Type.DEFAULT -> "기본음"
                Bell.Type.USER -> "사용자 지정음"
            }
        }
    }

//    fun setAlarmAndComment(alarmText: String, commentText: String) {
//        act.tvAlarm.text = alarmText
//        act.tvComment.text = commentText
//    }

    private fun setBottomDialog(mainText: String, subText: String) {
        with(act) {
            clBottomAlarm.visibility = View.VISIBLE
            act.tvBottomAlarmMainTitle.text = mainText
            act.tvBottomAlarmSubTitle.text = subText
        }
    }

    fun showBottomDialogTimeEndMessage(curCnt: Int, lastCnt: Int) {
        if (en()) {
            setBottomDialog("End ${curCnt} timer", "$curCnt/$lastCnt")
        } else {
            setBottomDialog("${curCnt}번째 타이머 종료", "$curCnt/$lastCnt")
        }
    }

    fun showBottomDialogRepeatEndMessage(repeatCnt: Int) {
        if (en()) {
            setBottomDialog("End repeat time set", "${repeatCnt} Repetitions")
        } else {
            setBottomDialog("반복 타임셋 종료", "${repeatCnt}회 반복")
        }
    }

    fun hideBottomDialog() {
        act.clBottomAlarm.visibility = View.INVISIBLE
    }

    private fun setBottomButtonText(leftStr: String, rightStr: String) {
        act.btBottom1Btn.visibility = View.VISIBLE
        act.btBottom2Btn.visibility = View.VISIBLE
        act.btBottom1Btn.text = leftStr
        act.btBottom2Btn.text = rightStr
    }

    private fun setCancelAndPauseStatus(isProc: Boolean) {

        if (en()) {
            setBottomButtonText(if (isProc) "cancel" else "end", "pause")
        } else {
            setBottomButtonText(if (isProc) "취소" else "종료", "일시정지")
        }

        act.btBottom2Btn.background = act.getDrawable(R.drawable.bg_bottom_button_gray)
        act.btBottom2Btn.setTextColor(R.color.ux_black.color())
        setSubtitle(View.INVISIBLE, "")
    }

    private fun setCancelAndRestartStatus(isProc: Boolean) {
        if (en()) {
            setBottomButtonText(if (isProc) "cancel" else "end", "start")
        } else {
            setBottomButtonText(if (isProc) "취소" else "종료", "시작")
        }

        act.btBottom2Btn.background = act.getDrawable(R.drawable.bg_bottom_button_red)
        act.btBottom2Btn.setTextColor(R.color.white.color())
        setSubtitle(View.VISIBLE, act.getString(R.string.pause))
    }

    fun showBottomBtn(procStatus: ProcStatus, isProc: Boolean) {
        if (procStatus == ProcStatus.ING || procStatus == ProcStatus.READY) setCancelAndPauseStatus(isProc)
        else if (procStatus == ProcStatus.PAUSE) setCancelAndRestartStatus(isProc)
    }

    fun setContentToHalfTransparent(isTransparent: Boolean) {
        if (isTransparent) act.viewHalfTransparent.visibility = View.VISIBLE
        else act.viewHalfTransparent.visibility = View.GONE
    }

    fun showMemo(memo: String = "") {
        with(act) {
            clMemo.visibility = View.VISIBLE
            etMemo.setText(memo)
            tvExceedErrorText.visibility = View.INVISIBLE
            tvExceedNumber.text = memo.length.toString()
        }
    }

    fun setMemoRemainByte(curByte: Int) {
        act.tvExceedNumber.text = curByte.toString()
        if (curByte > 999) {
            act.tvExceedNumber.setTextColor(R.color.ux_black.color())
            return
        }
        act.tvExceedNumber.setTextColor(R.color.ux_black.color())
    }

    fun hideMemo() {
        act.clMemo.visibility = View.GONE

        val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(act.clMemo.windowToken, 0)
    }
}