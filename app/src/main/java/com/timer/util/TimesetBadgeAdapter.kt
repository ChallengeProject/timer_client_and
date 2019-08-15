package com.timer.util

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timer.R
import kotlinx.android.synthetic.main.item_timeset_badge.view.*

class TimesetBadgeAdapter(
    val context: Context,
    val clickedCb: (Int, View) -> Unit
) : RecyclerView.Adapter<TimesetBadgeAdapter.VH>() {

    private var items = mutableListOf<TimesetBadge>()

    fun addBadge(timesetBadge: TimesetBadge) {
        items.add(timesetBadge)
    }

    fun getBadges() = items
    fun getBadge(pos: Int) = items[pos]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_timeset_badge, parent, false), clickedCb)
    }

    /**
     * set focus to position
     */
    fun resetFocus(position: Int) {
        removeFocus()
        items[position].type = TimesetBadgeType.FOCUS
        notifyItemChanged(position)
    }

    fun setDownArrowToPosition(position: Int) {
        if (items[position].type == TimesetBadgeType.FOCUS) {
            items[position].type = TimesetBadgeType.FOCUS_WITH_TOP_ICON
            notifyItemChanged(position)
        } else if (items[position].type == TimesetBadgeType.NORMAL) {
            items[position].type = TimesetBadgeType.NORMAL_WITH_TOP_ICON
            notifyItemChanged(position)
        }
    }

    fun hideDownArrow() {

        for (i in 0 until items.size) {
            if (items[i].type == TimesetBadgeType.FOCUS_WITH_TOP_ICON) {
                items[i].type = TimesetBadgeType.FOCUS
                notifyItemChanged(i)
            } else if (items[i].type == TimesetBadgeType.NORMAL_WITH_TOP_ICON) {
                items[i].type = TimesetBadgeType.NORMAL
                notifyItemChanged(i)
            }
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.tvName.text = items[position].getStringUsingFormat()
        holder.tvBottomNumber.text = "${position + 1}"

        if (items[position].type == TimesetBadgeType.NORMAL || items[position].type == TimesetBadgeType.NORMAL_WITH_TOP_ICON) {
            holder.tvName.setTextColor(Color.BLACK)
            holder.tvName.setBackgroundResource(R.drawable.bg_unselected_badge)
        } else {
            holder.tvName.setTextColor(Color.WHITE)
            holder.tvName.setBackgroundResource(R.drawable.bg_selected_badge)
        }

        when (items[position].type) {
            TimesetBadgeType.FOCUS_WITH_TOP_ICON, TimesetBadgeType.NORMAL_WITH_TOP_ICON ->
                holder.ivTopIcon.visibility = View.VISIBLE
            TimesetBadgeType.FOCUS, TimesetBadgeType.NORMAL -> holder.ivTopIcon.visibility = View.INVISIBLE
        }
    }

    /**
     * set all badge's type to normal == remove focus type
     */
    fun removeFocus() {
        for (i in 0 until items.size) {
            if (items[i].type == TimesetBadgeType.FOCUS || items[i].type == TimesetBadgeType.FOCUS_WITH_TOP_ICON) {
                items[i].type = TimesetBadgeType.NORMAL
                notifyItemChanged(i)
                return
            }
        }
    }

    inner class VH(view: View, cb: (Int, View) -> Unit) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                cb.invoke(adapterPosition, itemView)
            }
        }

        val ivTopIcon = view.ivTopIcon
        val tvName = view.tvName
        val tvBottomNumber = view.tvBottomNumber
    }


}