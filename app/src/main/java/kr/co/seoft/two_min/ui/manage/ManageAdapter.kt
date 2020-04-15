package kr.co.seoft.two_min.ui.manage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_manage.view.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.util.toTimeStr
import kr.co.seoft.two_min.util.x1000L


class ManageAdapter(val isCurrent: Boolean, val cb: (ManageCallbackType, ItemVH) -> Unit) :
    ListAdapter<TimeSet, ManageAdapter.ItemVH>(object : DiffUtil.ItemCallback<TimeSet>() {
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
                .inflate(R.layout.item_manage, parent, false),
            isCurrent,
                    cb
        )
    }


    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemVH(itemView: View, val isCurrent: Boolean, val cb: (ManageCallbackType, ItemVH) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val tvWholeTime = itemView.itemManageTvWholeTime
        val tvTitle = itemView.itemManageTvTitle
        val ivAddIcon = itemView.itemManageIvAddIcon
        val ivRemoveIcon = itemView.itemManageIvRemoveIcon
        val ivChangeIcon = itemView.itemManageIvChange

        @SuppressLint("ClickableViewAccessibility")
        fun bind(timeSet: TimeSet) {
            tvWholeTime.text = timeSet.wholeTime.x1000L().toTimeStr()
            tvTitle.text = timeSet.title

            if(isCurrent) {
                ivAddIcon.visibility = View.GONE
                ivRemoveIcon.visibility = View.VISIBLE

                ivRemoveIcon.setOnClickListener {
                    cb.invoke(ManageCallbackType.REMOVE, this)
                }

            } else {
                ivAddIcon.visibility = View.VISIBLE
                ivRemoveIcon.visibility = View.GONE

                ivAddIcon.setOnClickListener {
                    cb.invoke(ManageCallbackType.ADD, this)
                }

            }

            ivChangeIcon.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN)
                    cb.invoke(ManageCallbackType.MOVE, this)
                false
            }

        }
    }

    enum class ManageCallbackType {
        MOVE,
        ADD,
        REMOVE
    }
}