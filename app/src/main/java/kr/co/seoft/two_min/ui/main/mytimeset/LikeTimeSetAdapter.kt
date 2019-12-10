package kr.co.seoft.two_min.ui.main.mytimeset

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_like_time_set.view.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.util.toEndTimeStrAfterSec
import kr.co.seoft.two_min.util.toTimeStr
import kr.co.seoft.two_min.util.x1000L

class LikeTimeSetAdapter(private val cb: (TimeSet) -> Unit) :
    ListAdapter<TimeSet, LikeTimeSetAdapter.ItemVH>(object : DiffUtil.ItemCallback<TimeSet>() {
        override fun areItemsTheSame(oldItem: TimeSet, newItem: TimeSet): Boolean {
            return oldItem.timeSetId == newItem.timeSetId
        }

        override fun areContentsTheSame(oldItem: TimeSet, newItem: TimeSet): Boolean {
            return (oldItem.title == newItem.title) && (oldItem.wholeTime == newItem.wholeTime)
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        return ItemVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_like_time_set, parent, false),
            cb
        )
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemVH(itemView: View, private val cb: (TimeSet) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val tvWholeTime = itemView.itemLikeTimeSetTvWholeTime
        val tvEndTime = itemView.itemLikeTimeSetTvEndTime
        val tvTitle = itemView.itemLikeTimeSetTvTitle
        val tvTimeCount = itemView.itemLikeTimeSetTvTimeCount

        fun bind(timeSet: TimeSet) {
            tvWholeTime.text = timeSet.wholeTime.x1000L().toTimeStr()
            tvEndTime.text = "종료 예정 ${timeSet.wholeTime.toEndTimeStrAfterSec()}"
            tvTitle.text = timeSet.title
            tvTimeCount.text = timeSet.times.size.toString()

            itemView.setOnClickListener {
                cb.invoke(timeSet)
            }
        }
    }
}