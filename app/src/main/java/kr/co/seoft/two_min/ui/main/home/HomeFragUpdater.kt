package kr.co.seoft.two_min.ui.main.home

import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.util.*

class HomeFragUpdater(val f: HomeFragment) {

    fun showControlButton() {
        f.fragHomeTvCancel.visibility = View.VISIBLE
        f.fragHomeTvHour.visibility = View.VISIBLE
        f.fragHomeTvMinute.visibility = View.VISIBLE
        f.fragHomeTvSecond.visibility = View.VISIBLE
    }

    fun hideControlButton() {
        f.fragHomeTvCancel.visibility = View.INVISIBLE
        f.fragHomeTvHour.visibility = View.INVISIBLE
        f.fragHomeTvMinute.visibility = View.INVISIBLE
        f.fragHomeTvSecond.visibility = View.INVISIBLE
//        setMainTextAndEtc(0, false) // 191208 주석처리, 왜 넣었는지 기억이안남
        setSubText("")
    }

    fun showRv() {
        f.fragHomeRv.visibility = View.VISIBLE
        f.act.setShowBottomButtons(true)
        f.act.setLockViewpager(true)
    }

    fun hideRv() {
        f.fragHomeRv.visibility = View.INVISIBLE
        f.act.setShowBottomButtons(false)
        f.act.setLockViewpager(false)
    }

    fun setMainTextAndEtc(time: Long, setShowAddFromTimeValue: Boolean = true) {
        if (setShowAddFromTimeValue) {
            if (time == 0L) {
                f.fragHomeRv.hideAddButton()
                setMainTextClearBtnVisible(false)
            } else {
                f.fragHomeRv.showAddButton()
                setMainTextClearBtnVisible(true)
            }
        }

        f.fragHomeTvMain.text = time.toFormattingString()
    }

    fun setMainTextClearBtnVisible(b: Boolean) {
        f.fragHomeIvMainClear.visibility = b.setVisible()
    }

    fun setSubText(str: String) {
        f.fragHomeTvSub.text = str
    }

    fun setSubText(str1: String, str2: String) {
        f.fragHomeTvSub.text = "타임셋 시간 $str1        종료 예정 $str2"
    }

    fun showTimeInfo(index: Int, homeBadges: MutableList<HomeBadge>) {

        val time = homeBadges[index].time

        f.fragHomeLlTimeInfo.visibility = View.VISIBLE
        f.fragHomeIvCloseTimeInfo.visibility = View.VISIBLE
        f.fragHomeViewTransparent.visibility = View.VISIBLE

        f.fragHomeEtContent.text = time.comment.toEditable()
        f.fragHomeTvContentLength.text = "${time.comment.length}"

        f.fragHomeTvTimerPosition.text = "${index}번째 타이머"

        f.fragHomeTvSetWhole.paintFlags = (f.fragHomeTvSetWhole.paintFlags or Paint.UNDERLINE_TEXT_FLAG)

        setBottomLineClearToAllBellType()
        when (time.bell.type) {
            Bell.Type.DEFAULT -> {
                setBottomLineToBasicBell()
            }
            Bell.Type.SLIENT -> {
                setBottomLineToSlient()
            }
            Bell.Type.VIBRATION -> {
                setBottomLineToVibrate()
            }
        }

        f.fragHomeIncSelectedHomeBadge.findViewById<TextView>(R.id.itemHomeBadgeTvTime).text = time.seconds.x1000L().toTimeStr()
        f.fragHomeIncSelectedHomeBadge.findViewById<TextView>(R.id.itemHomeBadgeTvCount).text = "${index}/${homeBadges.size - 2}"
        f.fragHomeIncSelectedHomeBadge.findViewById<LinearLayout>(R.id.itemHomeBadgellContent).background =
            ContextCompat.getDrawable(f.requireContext(), R.drawable.bg_timeset_times_red_stroke)
        f.act.setTransparentToolbarAndBottoms(true)
    }

    fun setEnable(tv: TextView, isEnable: Boolean) {
        if (isEnable) {
            tv.isClickable = true
            tv.setTextColor(R.color.ux_black.color())
        } else {
            tv.isClickable = false
            tv.setTextColor(R.color.text_hint_gray.color())
        }
    }

    fun hideTimeInfo() {
        f.fragHomeLlTimeInfo.visibility = View.INVISIBLE
        f.fragHomeIvCloseTimeInfo.visibility = View.INVISIBLE
        f.fragHomeViewTransparent.visibility = View.INVISIBLE
        f.act.setTransparentToolbarAndBottoms(false)
    }

    fun setBottomLineToSlient() {
        f.fragHomeTvSilent.background = ContextCompat.getDrawable(f.requireContext(), R.drawable.bg_bottom_red_bar)
    }

    fun setBottomLineToVibrate() {
        f.fragHomeTvVibrate.background = ContextCompat.getDrawable(f.requireContext(), R.drawable.bg_bottom_red_bar)
    }

    fun setBottomLineToBasicBell() {
        f.fragHomeTvBasicBell.background = ContextCompat.getDrawable(f.requireContext(), R.drawable.bg_bottom_red_bar)
    }

    fun setBottomLineClearToAllBellType() {
        f.fragHomeTvVibrate.background = null
        f.fragHomeTvSilent.background = null
        f.fragHomeTvBasicBell.background = null
    }

}