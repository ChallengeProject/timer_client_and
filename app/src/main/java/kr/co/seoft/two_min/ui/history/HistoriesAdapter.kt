package kr.co.seoft.two_min.ui.history

import android.view.*
import android.view.animation.AlphaAnimation
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_histories.view.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.History
import kr.co.seoft.two_min.util.toTimeStr
import kr.co.seoft.two_min.util.x1000L

class HistoriesAdapter(val cb: (Pair<History,Type>) -> Unit) :
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

    class ItemVH(itemView: View, val cb: (Pair<History,Type>) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val tvWholeTime = itemView.itemHistoriesTvWholeTime
        val tvTitle = itemView.itemHistoriesTvTitle
        val tvUseData = itemView.itemHistoriesTvUseDate
        val clDelete = itemView.itemHistoriesClDelete

        fun bind(history: History) {
            tvWholeTime.text = history.wholeTime.x1000L().toTimeStr()
            tvTitle.text = history.timeSetTitle
            tvUseData.text = history.ymdString
            clDelete.visibility = View.INVISIBLE

            val gestureDetector = GestureDetector(itemView.context, object : GestureDetector.OnGestureListener {
                override fun onShowPress(p0: MotionEvent?) {}

                override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                    if (clDelete.isVisible) {
                        clDelete.visibility = View.INVISIBLE
                    } else {
                        cb.invoke(Pair(history,Type.OPEN))
                    }
                    return true
                }

                override fun onDown(p0: MotionEvent?): Boolean {
                    return true
                }

                override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                    if (p2 < 0) {
                        clDelete.visibility = View.VISIBLE
                        clDelete.animation = AlphaAnimation(0f, 1f).apply {
                            duration = 300
                        }
                    }
                    return true
                }

                override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                    if (p2 > 0) {
                        clDelete.visibility = View.VISIBLE
                        clDelete.animation = AlphaAnimation(0f, 1f).apply {
                            duration = 300
                        }
                    }
                    return true
                }

                override fun onLongPress(p0: MotionEvent?) {}

            })

            clDelete.setOnClickListener {
                cb.invoke(Pair(history,Type.DELETE))
            }

            itemView.setOnTouchListener { view, motionEvent ->
                gestureDetector.onTouchEvent(motionEvent)
                true
            }
        }
    }

    enum class Type{
        OPEN,
        DELETE
    }
}