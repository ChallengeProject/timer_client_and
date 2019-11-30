package kr.co.seoft.two_min.ui.main.home


import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.*

class MiddleTransformSnappyRecyclerView : RecyclerView {

    companion object {
        const private val LEFT_MID_POS = 3
    }

    private lateinit var snapHelper: SnapHelper
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var homeBadgeAdapter: HomeBadgeAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    // +1 -1 안해도됨 어차피 0인덱스 마지막인덱스까지 고려된 position 이라서...
    // 그래서 첫초기화때 [REPEAT],[0초를 가지고있는 NORMAL],[ADD_SHOW] 세개중에 init index는 1
    private var focusingPos = 1

    var onBadgeSelectedListener: ((HomeBadgeCallbackType, Int) -> Unit)? = null

    constructor(context: Context) : this(context, null) {
        initHorizontalHomeList(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initHorizontalHomeList(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initHorizontalHomeList(context)
    }

    private fun initHorizontalHomeList(
        context: Context
    ) {
        linearLayoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        this.layoutManager = linearLayoutManager
        homeBadgeAdapter =
            HomeBadgeAdapter(context) { type, vh ->
                val curPos = getCurPos()

                val pos = vh.adapterPosition

                // pushed Add button
                if (type == HomeBadgeCallbackType.ADD_PUSH || type == HomeBadgeCallbackType.NORMAL_PUSH) {
                    if (type == HomeBadgeCallbackType.ADD_PUSH) onBadgeSelectedListener?.invoke(type, pos)
                    homeBadgeAdapter.resetFocus(pos)
                    focusingPos = pos

                    // smoothScrollToPosition not accuracy depending on position, so add, subject value
                    if (pos >= curPos) this.smoothScrollToPosition(pos + 1)
                    else this.smoothScrollToPosition(pos - 1)

                    if (type == HomeBadgeCallbackType.NORMAL_PUSH) {
                        onBadgeSelectedListener?.invoke(type, pos)
                    }

                    return@HomeBadgeAdapter
                } else if (type == HomeBadgeCallbackType.LONG_PUSH) {
                    itemTouchHelper.startDrag(vh)
                } else if (type == HomeBadgeCallbackType.FOCUS_PUSH) {
                    onBadgeSelectedListener?.invoke(type, pos)
                }
            }

        this.adapter = homeBadgeAdapter
        snapHelper = LinearSnapHelper()

        // ref : https://stackoverflow.com/questions/44043501/an-instance-of-onflinglistener-already-set-in-recyclerview
        this.onFlingListener = null
        snapHelper.attachToRecyclerView(this)

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback({ from, to ->

            // 다음 if문 : 첫번째 마지막번째 버튼들있는 count에서 예외처리
            if (from.adapterPosition == 0 || to.adapterPosition == homeBadgeAdapter.itemCount
                || to.adapterPosition == 0 || from.adapterPosition == homeBadgeAdapter.itemCount
            ) {
                return@ItemTouchHelperCallback
            }

            homeBadgeAdapter.onItemMoved(from.adapterPosition, to.adapterPosition)
        }, {
            // call when move end after long click, for refresh badge's bottom numbers

            homeBadgeAdapter.findFocusBadge()?.let {
                focusingPos = it
            }

            homeBadgeAdapter.notifyDataSetChanged()
        }))
        itemTouchHelper.attachToRecyclerView(this)

        this.smoothScrollToPosition(LEFT_MID_POS)
    }

    fun getFocusingBadge() = homeBadgeAdapter.getBadge(focusingPos)

    fun resetBadges() {
        focusingPos = 1
        homeBadgeAdapter.resetBadges()
        homeBadgeAdapter.notifyDataSetChanged()
    }

    fun setFocusingBadge(homeBadge: HomeBadge) {
        homeBadgeAdapter.setBadge(focusingPos, homeBadge)
        homeBadgeAdapter.notifyDataSetChanged()
    }
    fun hideAddButton(){
       homeBadgeAdapter.hideAddButton()
    }

    fun showAddButton(){
        homeBadgeAdapter.showAddButton()
    }


    @Deprecated("안정화시 삭제 예정, 무조건 빈 HomeBadge로 들어가서 인자를 받을 필요가 없음")
    fun addHomeBadge(homeBadge: HomeBadge) {
        if (homeBadge.type == HomeBadgeType.ADD_SHOW) return
        homeBadgeAdapter.addBadge(homeBadge)
        homeBadgeAdapter.notifyDataSetChanged()
    }

    fun addHomeBadge() {
        homeBadgeAdapter.addBadge(HomeBadge(second = 0, type = HomeBadgeType.NORMAL))
        homeBadgeAdapter.notifyDataSetChanged()
    }

    fun addHomeBadges(homeBadges: Array<HomeBadge>) {
        homeBadges.forEach {
            if (it.type == HomeBadgeType.NORMAL) homeBadgeAdapter.addBadge(it)
        }
        homeBadgeAdapter.notifyDataSetChanged()
    }

    private fun getCurPos() =
        (linearLayoutManager.findLastCompletelyVisibleItemPosition() + linearLayoutManager.findFirstCompletelyVisibleItemPosition()) / 2

    fun getBadges() = homeBadgeAdapter.getBadges()
    fun getBadge(index: Int) = homeBadgeAdapter.getBadge(index)
    fun removeZeroSecondBadge() {
        homeBadgeAdapter.removeZeroSecondBadge()
    }

}