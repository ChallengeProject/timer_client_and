package kr.co.seoft.two_min.ui.main.home


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_home_badge.view.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.data.Time
import kr.co.seoft.two_min.util.e

class HomeBadgeAdapter(
    val context: Context,
    private val cb: (HomeBadgeCallbackType, VH) -> Unit
) : RecyclerView.Adapter<HomeBadgeAdapter.VH>() {

    companion object {
        val INIT_HOME_BADGES = mutableListOf(
            HomeBadge(Time(0), HomeBadgeType.REPEAT_OFF),
            HomeBadge(Time(0), HomeBadgeType.FOCUS),
            HomeBadge(Time(0), HomeBadgeType.ADD_SHOW)
        )
    }

    private var items = INIT_HOME_BADGES.map{it.copy()}.toMutableList()

    fun resetBadges() {
        items = INIT_HOME_BADGES.map{it.copy()}.toMutableList()
    }

    /**
     * for Dispose of between empty's badge
     * default : 1
     * with add button : 3
     * 이 숫자가 [size-positionController] 에 아이템이 들어간다는거
     * 기존에 normal 왼쪽 오른쪽에 dummy를 여러개뒀을때 const화가 필요해서 추가하긴했음
     * 안정화시 삭제 예정
     */
    private var positionController = 1

    /**
     * add badge
     *  - for insert between in dummy badge
     */
    fun addBadge(homeBadge: HomeBadge) {
        items.add(items.size - positionController, homeBadge)
    }

    fun setBadge(index: Int, homeBadge: HomeBadge) {
        items[index] = homeBadge
    }

    fun getBadges() = items
    fun getBadge(pos: Int) = items[pos]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_home_badge, parent, false), cb)
    }

    fun hideAddButton() {
        items.last().type = HomeBadgeType.ADD_HIDE
        notifyDataSetChanged()
    }

    fun showAddButton() {
        items.last().type = HomeBadgeType.ADD_SHOW
        notifyDataSetChanged()
    }


    fun setBellType(bellType: Bell.Type, position: Int) {
        items[position].time.bell.type = bellType
    }

    /**
     * set focus to position
     */
    fun resetFocus(position: Int) {
        removeFocus()
        items[position].type = HomeBadgeType.FOCUS
        notifyItemChanged(position)
    }

    fun onItemMoved(from: Int, to: Int) {
        if (from == to || items[to].type == HomeBadgeType.EMPTY
            || items[to].type == HomeBadgeType.ADD_SHOW
            || items[to].type == HomeBadgeType.ADD_HIDE
            || items[to].type == HomeBadgeType.REPEAT_OFF
            || items[to].type == HomeBadgeType.REPEAT_ON
        ) {
            return
        }

        val fromItem = items.removeAt(from)
        items.add(to, fromItem)
        notifyItemMoved(from, to)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.setData(items[position], items.size)
    }

    /**
     * set all badge's type to normal == remove focus type
     */
    fun removeFocus() {
        for (i in 0 until items.size) {
            if (items[i].type == HomeBadgeType.FOCUS) {
                items[i].type = HomeBadgeType.NORMAL
                notifyItemChanged(i)
                return
            }
        }
    }

    fun findFocusBadge(): Int? {

        for (i in 0 until items.size) {
            if (items[i].type == HomeBadgeType.FOCUS) {
                return i
            }
        }
        return null
    }

    fun removeZeroSecondBadge() {
        items.removeAll { it.time.seconds == 0 && it.type == HomeBadgeType.NORMAL }
    }

    fun removeBadge(position: Int){

        "removeBadge $position".e()
        items.removeAt(position)
    }

    fun setCurPosBellTypeToWhole(position: Int) {
        val curBellType = items[position].time.bell.type
        items.forEach { it.time.bell.type = curBellType }
    }

    fun setComment(comment: String, position: Int) {
        items[position].time.comment = comment
    }

    fun setSecond(second: Int, pos: Int) {
        items[pos].time.seconds = second
    }

    inner class VH(view: View, cb: (HomeBadgeCallbackType, VH) -> Unit) :
        RecyclerView.ViewHolder(view) {

        // ref :
        // http://dudmy.net/android/2017/06/23/consider-of-recyclerview/
        // > onBindViewHolder 에 listener 성능 저하, onCreateViewHolder 혹은 RecyclerView.ViewHolder내 callback 처리

        private val itemHomeBadgellContent = view.itemHomeBadgellContent
        private val itemHomeBadgeIvAdd = view.itemHomeBadgeIvAdd
        private val itemHomeBadgeIvRepeat = view.itemHomeBadgeIvRepeat
        private val itemHomeBadgeTvTime = view.itemHomeBadgeTvTime
        private val itemHomeBadgeTvCount = view.itemHomeBadgeTvCount

        init {
            itemView.setOnClickListener {

                when (items[adapterPosition].type) {
                    HomeBadgeType.NORMAL -> cb.invoke(HomeBadgeCallbackType.NORMAL_PUSH, this)
                    HomeBadgeType.FOCUS -> cb.invoke(HomeBadgeCallbackType.FOCUS_PUSH, this)
                    HomeBadgeType.ADD_SHOW -> cb.invoke(HomeBadgeCallbackType.ADD_PUSH, this)
                    HomeBadgeType.REPEAT_OFF -> cb.invoke(HomeBadgeCallbackType.REPEAT_OFF_PUSH, this)
                    HomeBadgeType.REPEAT_ON -> cb.invoke(HomeBadgeCallbackType.REPEAT_ON_PUSH, this)
                }
            }

            itemView.setOnLongClickListener {
                if (items[adapterPosition].type == HomeBadgeType.NORMAL
                    || items[adapterPosition].type == HomeBadgeType.FOCUS
                ) {
                    cb.invoke(HomeBadgeCallbackType.LONG_PUSH, this)
                }
                false
            }
        }

        fun isVisible(b: Boolean): Int {
            return if (b) View.VISIBLE
            else View.INVISIBLE
        }

        fun visibleViews(all: Boolean, content: Boolean, add: Boolean, repeat: Boolean) {
            itemView.visibility = isVisible(all)
            itemHomeBadgeIvAdd.visibility = isVisible(add)
            itemHomeBadgeIvRepeat.visibility = isVisible(repeat)
            itemHomeBadgellContent.visibility = isVisible(content)
        }

        fun setData(homeBadge: HomeBadge, wholeCount: Int) {

            /**
             * set view's style from each HomeBadgeType
             */
            if (homeBadge.type == HomeBadgeType.EMPTY) {
                itemView.visibility = View.INVISIBLE
                return
            } else {
                itemView.visibility = View.VISIBLE
            }

            when (homeBadge.type) {
                HomeBadgeType.REPEAT_ON -> {
                    visibleViews(true, false, false, true)
                }
                HomeBadgeType.REPEAT_OFF -> {
                    visibleViews(true, false, false, true)
                }
                HomeBadgeType.ADD_SHOW -> {
                    visibleViews(true, false, true, false)
                }
                HomeBadgeType.ADD_HIDE -> {
                    visibleViews(true, false, false, false)
                }
                HomeBadgeType.EMPTY -> {
                    visibleViews(false, false, false, false)
                }
                HomeBadgeType.NORMAL -> {
                    visibleViews(true, true, false, false)
                    itemHomeBadgeTvTime.text = homeBadge.getStringUsingFormat()
                    itemHomeBadgeTvCount.text = "${adapterPosition}/${wholeCount - 2}"
                    itemHomeBadgellContent.setBackgroundResource(R.drawable.bg_timeset_times_gray_stroke)
                }
                HomeBadgeType.FOCUS -> {
                    visibleViews(true, true, false, false)
                    itemHomeBadgeTvTime.text = homeBadge.getStringUsingFormat()
                    itemHomeBadgeTvCount.text = "${adapterPosition}/${wholeCount - 2}"
                    itemHomeBadgellContent.setBackgroundResource(R.drawable.bg_timeset_times_red_stroke)
                }
            }
        }
    }
}