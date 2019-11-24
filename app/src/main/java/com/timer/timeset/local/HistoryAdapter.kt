package com.timer.timeset.local

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timer.R
import com.timer.se_util.e

class HistoryAdapter(val context: Context, private val hisotryViewModel: HistoryViewModel, val cb: (String) -> Unit) :
    ListAdapter<String, HistoryAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }) {

    fun setVM(myTimeSetListFragment: MyTimeSetListFragment) {
        hisotryViewModel.observableText.observe(myTimeSetListFragment, Observer {
            it.e()
        })
        hisotryViewModel.observableHistories.observe(myTimeSetListFragment, Observer {
            submitList(it)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)

        return ViewHolder(view, cb)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    class ViewHolder(itemView: View, val cb: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val itemHistoryTvContent = itemView.findViewById<TextView>(R.id.itemHistoryTvContent)

        fun setItem(content: String) {
            itemHistoryTvContent.text = content

            itemView.setOnClickListener { cb.invoke(content) }
        }
    }
}
