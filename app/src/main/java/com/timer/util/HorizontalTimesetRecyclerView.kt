package com.timer.util

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HorizontalTimesetRecyclerView : RecyclerView {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var timesetBadgeAdapter: TimesetBadgeAdapter

    /**
     * return (pos , topIconXCoordinate)
     * if not focus with topIcon, second return value -1
     *
     */
    var onBadgeSelectedListener: ((Int,Int) -> Unit)? = null

    var latelyPos = 0

    constructor(context: Context) : this(context, null) {
        initHorizontalTimesetList(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initHorizontalTimesetList(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initHorizontalTimesetList(context)
    }

    fun showTopIcon(){
        if(timesetBadgeAdapter.getBadge(latelyPos).type == TimesetBadgeType.FOCUS)
            timesetBadgeAdapter.setTopIconOnFocus(latelyPos)
    }

    private fun initHorizontalTimesetList(context: Context) {

        linearLayoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        this.layoutManager = linearLayoutManager
        timesetBadgeAdapter =
            TimesetBadgeAdapter(context, { pos ->
                timesetBadgeAdapter.resetFocus(pos)
                latelyPos = pos
                this.smoothScrollToPosition(pos)
                onBadgeSelectedListener?.invoke(latelyPos,-1)
            }, {
                onBadgeSelectedListener?.invoke(latelyPos,it)
            }
            )
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