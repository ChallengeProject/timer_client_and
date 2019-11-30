package kr.co.seoft.two_min.ui.main.home

import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.data.Time
import kr.co.seoft.two_min.util.color
import kr.co.seoft.two_min.util.toEditable
import kr.co.seoft.two_min.util.toTimeStr
import kr.co.seoft.two_min.util.x1000L

// TODO 뷰모델도받자 데이터가져와야되서
class HomeFragUpdater(val f: HomeFragment) {

    fun setMainText(str: String) {
        f.fragHomeTvMain.text = str
    }

    fun setMainTextClearBtnVisible(b: Boolean) {
        f.fragHomeIvMainClear.visibility = if (b) View.VISIBLE else View.INVISIBLE
    }

    fun setSubText(str: String) {
        f.fragHomeTvSub.text = str
    }

    fun setSubText(str1: String, str2: String) {
        f.fragHomeTvSub.text = "$str1     $str2"
    }

    fun showTimeInfo(index: Int) {

        //TODO 뷰모델에서 인덱스 대비 가져와야댐
        val time = Time(123, "Aa", Bell(Bell.Type.DEFAULT, null))

        f.fragHomeLlTimeInfo.visibility = View.VISIBLE
        f.fragHomeIvCloseTimeInfo.visibility = View.VISIBLE
        f.fragHomeViewTransparent.visibility = View.VISIBLE

        f.fragHomeEtContent.text = time.comment.toEditable()
        f.fragHomeTvContentLength.text = "${time.comment.length}/100 bytes"

        f.fragHomeTvTimerPosition.text = "${index + 1}번째 타이머"

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
        f.fragHomeIncSelectedHomeBadge.findViewById<LinearLayout>(R.id.itemHomeBadgellContent).background =
            ContextCompat.getDrawable(f.requireContext(), R.drawable.bg_timeset_times_red_stroke)
        f.act.setTransparentToolbarAndBottoms(true)

    }

    fun setEnable(tv: TextView, isEnable: Boolean) {
        if(isEnable) {
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