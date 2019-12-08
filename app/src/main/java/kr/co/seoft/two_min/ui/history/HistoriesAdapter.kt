package kr.co.seoft.two_min.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_histories.view.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.History
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.util.toTimeStr
import kr.co.seoft.two_min.util.x1000L

class HistoriesAdapter(val cb: (History) -> Unit) :
    ListAdapter<History, HistoriesAdapter.ItemVH>(object : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.historyId == newItem.historyId
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.historyId == newItem.historyId
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        return ItemVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_histories, parent, false),
            cb
        )
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemVH(itemView: View, val cb: (History) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val tvWholeTime = itemView.itemHistoriesTvWholeTime
        val tvTitle = itemView.itemHistoriesTvTitle
        val tvUseData = itemView.itemHistoriesTvUseDate

        fun bind(history: History) {
            tvWholeTime.text = history.wholeTime.x1000L().toTimeStr()
            tvTitle.text = history.timeSetTitle
            tvUseData.text = history.ymdString

            itemView.setOnClickListener {
                cb.invoke(history)
            }

        }
    }

}