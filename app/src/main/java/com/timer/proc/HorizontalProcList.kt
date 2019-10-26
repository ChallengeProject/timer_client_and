package com.timer.proc

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.timer.se_util.i

class HorizontalProcList : RecyclerView {


    var snapHelper: SnapHelper
    var linearLayoutManager: LinearLayoutManager
    lateinit var procBadgeAdapter: ProcBadgeAdapter

    var WHOLE_COUNT = 4

    var LEFT_REACH_CONDITION = 0
    var LEFT_MID_POS = 3

    var RIGHT_REACH_CONDITION = WHOLE_COUNT - 1
    var RIGHT_MID_POS = WHOLE_COUNT - 4

    var onBadgeSelectedListener: ((Int) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {


        linearLayoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        this.layoutManager = linearLayoutManager
        procBadgeAdapter =
            ProcBadgeAdapter(context) { vh ->

                val pos = vh.adapterPosition
                procBadgeAdapter.resetFocus(pos)
                moveBadge(pos)
            }

        this.adapter = procBadgeAdapter
        snapHelper = LinearSnapHelper()

        // ref : https://stackoverflow.com/questions/44043501/an-instance-of-onflinglistener-already-set-in-recyclerview
        this.onFlingListener = null
        snapHelper.attachToRecyclerView(this)

        this.smoothScrollToPosition(LEFT_MID_POS)
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // adjust position when reach first or end, cant set middle position in onScrolled callback
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == LEFT_REACH_CONDITION)
                    this@HorizontalProcList.smoothScrollToPosition(LEFT_MID_POS)
                else if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == RIGHT_REACH_CONDITION)
                    this@HorizontalProcList.smoothScrollToPosition(RIGHT_MID_POS)
            }

        })
    }

    fun setFocus(pos: Int) {
        "setFocus $pos".i()
        val focusPos = pos + 1
        procBadgeAdapter.resetFocus(focusPos)

        moveBadge(focusPos)
    }

    fun moveBadge(movingPos: Int) {

        // smoothScrollToPosition not accuracy depending on position, so add, subject value
        if (movingPos >= getCurPos()) this.smoothScrollToPosition(movingPos + 1)
        else this.smoothScrollToPosition(movingPos - 1)

    }

    fun addHomeBadge(procBadge: ProcBadge) {
        procBadgeAdapter.addBadge(procBadge)
        refreshListAndRecalc()
    }

    fun addHomeBadges(homeBadges: Array<ProcBadge>) {
        homeBadges.forEach {
            procBadgeAdapter.addBadge(it)
        }
        refreshListAndRecalc()
    }

    fun refreshListAndRecalc() {
        procBadgeAdapter.notifyDataSetChanged()
        recalc()
    }

    /**
     * recalculate for set first, last badge when reach that
     */
    fun recalc() {
        WHOLE_COUNT = procBadgeAdapter.getBadges().size
        RIGHT_REACH_CONDITION = WHOLE_COUNT - 1
        RIGHT_MID_POS = WHOLE_COUNT - 4
    }


    fun getCurPos() =
        (linearLayoutManager.findLastCompletelyVisibleItemPosition() + linearLayoutManager.findFirstCompletelyVisibleItemPosition()) / 2

}