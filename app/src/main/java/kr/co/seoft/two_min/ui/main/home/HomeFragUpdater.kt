package kr.co.seoft.two_min.ui.main.home

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.data.Time
import kr.co.seoft.two_min.util.toEditable

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
    }

    fun hideTimeInfo() {
        f.fragHomeLlTimeInfo.visibility = View.INVISIBLE
        f.fragHomeIvCloseTimeInfo.visibility = View.INVISIBLE
        f.fragHomeViewTransparent.visibility = View.INVISIBLE
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