package com.timer.util

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HorizontalTimesetRecyclerView : RecyclerView {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var timesetBadgeAdapter: TimesetBadgeAdapter

    /**
     * return (pos)
     */
    var onBadgeSelectedListener: ((Int) -> Unit)? = null

    private var latelyPos = 0
    private var focusPos = -1

    var latelyMidPosX = 0 // use calculate skip view

    constructor(context: Context) : this(context, null) {
        initHorizontalTimesetList(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initHorizontalTimesetList(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initHorizontalTimesetList(context)
    }

    fun showDownArrowAndUpdateDownArrowPos() {
        if (timesetBadgeAdapter.getBadge(latelyPos).type == TimesetBadgeType.FOCUS) {
            timesetBadgeAdapter.setDownArrowToPosition(latelyPos)
        }
    }

    fun hideDownArrow() {
        if (timesetBadgeAdapter.getBadge(latelyPos).type == TimesetBadgeType.FOCUS_WITH_TOP_ICON) {
            timesetBadgeAdapter.hideDownArrowToPosition(latelyPos)
        }
    }

    fun getLatelyPos() = latelyPos
    fun getFocusPos() = focusPos

    fun setFocus(pos: Int) {
        focusPos = pos
        timesetBadgeAdapter.resetFocus(pos)
    }

    private fun initHorizontalTimesetList(context: Context) {

        linearLayoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        this.layoutManager = linearLayoutManager
        timesetBadgeAdapter =
            TimesetBadgeAdapter(context) { pos, view ->
                // call when clicked badge

//                timesetBadgeAdapter.resetFocus(pos)
                latelyPos = pos
                this.smoothScrollToPosition(pos)
                /**
                 * get mid position of selected badge for draw skip view
                 * adjust delay 150 ms for wait animation move delay time
                 * [latelyMidPosX] will use from outer place that use this recyclerview for set skip view's X position
                 */
                postDelayed({
                    val locationArr = intArrayOf(0, 0)
                    view.getLocationOnScreen(locationArr)

                    latelyMidPosX = locationArr[0] + view.width / 2
                    onBadgeSelectedListener?.invoke(latelyPos)
                }, 150)

            }

        this.adapter = timesetBadgeAdapter
        this.smoothScrollToPosition(0)
    }

    fun addTimesetBadge(timesetBadge: TimesetBadge) {
        if (timesetBadge.type == TimesetBadgeType.NORMAL) timesetBadgeAdapter.addBadge(timesetBadge)
        timesetBadgeAdapter.notifyDataSetChanged()
    }

    fun addTimesetBadges(timesetBadges: Array<TimesetBadge>) {
        timesetBadges.forEach {
            if (it.type == TimesetBadgeType.NORMAL) timesetBadgeAdapter.addBadge(it)
        }
        timesetBadgeAdapter.notifyDataSetChanged()
    }

}