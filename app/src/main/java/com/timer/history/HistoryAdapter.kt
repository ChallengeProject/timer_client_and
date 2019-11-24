package com.timer.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.timer.R
import com.timer.db.entity.TempHistoryEntity
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class HistoryAdapter(var histories: List<TempHistoryEntity>?) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.itemView.historyTitle.text = "$position history id: ${histories?.get(position)?.id}"
    }

    override fun getItemCount(): Int = histories?.size ?: 0

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick { Toast.makeText(itemView.context, "clicked", Toast.LENGTH_SHORT).show() }
        }
    }
}

