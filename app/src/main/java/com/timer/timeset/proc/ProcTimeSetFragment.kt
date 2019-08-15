package com.timer.timeset.proc

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.util.TimesetBadge
import com.timer.util.TimesetBadgeType
import com.timer.util.i
import kotlinx.android.synthetic.main.fragment_proc_timeset.*


class ProcTimeSetFragment : Fragment() {

    val TAG = "ProcTimeSetFragment"

    lateinit var times: ArrayList<Int>
    lateinit var cdt: PreciseCountdown

    var deviceWidth = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_proc_timeset, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDeviceSpec()
        initProperties()
        initViews()
    }

    fun initDeviceSpec() {
        val display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        deviceWidth = size.x
    }

    fun initProperties() {
        times = arrayListOf(30, 40, 50, 150)
    }

    fun initViews() {

        times.forEach {
            rvHTRV.addTimesetBadge(TimesetBadge(second = it, type = TimesetBadgeType.NORMAL))
        }

        tvTopExplain.text = "시작 5초 전"

        tvTime.text = "01:20:00"
        tvTimeSetName.text = "타임셋명"

        tvEndTime.text = "10:57 AM"
        tvWholeTime.text = "00시간 00분 00초"

        tvBell.text = "기본음"
        tvComment.text = "코맨트"

        btLeft.setOnClickListener { _ ->
            rvHTRV.showTopIconAndGetTopIconPos()

            showSkipView(rvHTRV.latelyMidPosX)
        }

        rvHTRV.onBadgeSelectedListener = { pos ->
            "onBadgeSelectedListener callback $pos".i(TAG)
        }

    }

    /**
     * show skip view with calculate X position
     */
    fun showSkipView(midPos: Int) {
        clSkipTimerMessage.visibility = View.VISIBLE
        var rst = deviceWidth - midPos - clSkipTimerMessage.width / 2
        if (rst + clSkipTimerMessage.width > deviceWidth) rst = deviceWidth - clSkipTimerMessage.width
        val p = clSkipTimerMessage.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(0, 0, rst, 0)
        clSkipTimerMessage.requestLayout()
    }

}