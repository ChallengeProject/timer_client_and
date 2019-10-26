package com.timer.proc

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timer.R
import com.timer.proc.ProcBadge.Companion.emptyProcBadge
import kotlinx.android.synthetic.main.item_proc_badge.view.*

class ProcBadgeAdapter(
    val context: Context,
    val cb: (VH) -> Unit
) : RecyclerView.Adapter<ProcBadgeAdapter.VH>() {

    companion object {
        private var items =
            mutableListOf(emptyProcBadge, emptyProcBadge, emptyProcBadge, emptyProcBadge)
    }

    /**
     * for Dispose of between empty's badge
     * default : 2
     * with add button : 3
     */
    private var positionController = 2

    /**
     * add badge
     *  - for insert between in dummy badge
     */
    fun addBadge(procBadge: ProcBadge) {
        items.add(items.size - positionController, procBadge)
    }

    fun getBadges() = items
    fun getBadge(pos: Int) = items[pos]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(context).inflate(R.layout.item_proc_badge, parent, false),
            cb
        ).apply {

            this.itemView.setOnClickListener {
                cb.invoke(this)
            }

        }
    }

    /**
     * set focus to position
     */
    fun resetFocus(position: Int) {
        removeFocus()
        items[position].type = ProcBadgeType.FOCUS
        notifyItemChanged(position)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        /**
         * set view's style from each HomeBadgeType
         */

        when (items[position].type) {
            ProcBadgeType.EMPTY -> holder.itemView.visibility = View.INVISIBLE
            ProcBadgeType.NORMAL -> {
                holder.itemView.visibility = View.VISIBLE
                holder.tvTime.text = items[position].getStringUsingFormat()
                holder.tvTime.setTextColor(Color.BLACK)
                holder.tvTime.setBackgroundColor(Color.GRAY)
                holder.tvTime.visibility = View.VISIBLE
                holder.tvCount.text = "${position - 1}/${items.size - 4}"
            }
            ProcBadgeType.FOCUS -> {
                holder.itemView.visibility = View.VISIBLE
                holder.tvTime.text = items[position].getStringUsingFormat()
                holder.tvTime.setTextColor(Color.WHITE)
                holder.tvTime.setBackgroundColor(Color.CYAN)
                holder.tvTime.visibility = View.VISIBLE
                holder.tvCount.text = "${position - 1}/${items.size - 4}"
            }
        }

    }

    /**
     * set all badge's type to normal == remove focus type
     */
    fun removeFocus() {
        for (i in 0 until items.size) {
            if (items[i].type == ProcBadgeType.FOCUS) {
                items[i].type = ProcBadgeType.NORMAL
                notifyItemChanged(i)
            }
        }
    }

    class VH(view: View, cb: (VH) -> Unit) : RecyclerView.ViewHolder(view) {

        // ref :
        // http://dudmy.net/android/2017/06/23/consider-of-recyclerview/
        // > onBindViewHolder 에 listener 성능 저하, onCreateViewHolder 혹은 RecyclerView.ViewHolder내 callback 처리
        init {
            itemView.setOnClickListener {
                cb.invoke(this)
            }
        }

        val tvCount = view.tvCount
        val tvTime = view.tvTime
    }

}