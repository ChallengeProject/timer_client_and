package com.timer.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.timer.R
import com.timer.db.entity.TempHistoryEntity
import kotlinx.android.synthetic.main.fragment_history_list.*

class HistoryListFragment : Fragment(), Observer<List<TempHistoryEntity>> {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_history_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyRecyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = HistoryAdapter(emptyList())
        }

//        val viewModel = HistoryViewModel()
//        viewModel.histories.observe(this, this)
    }

    override fun onChanged(t: List<TempHistoryEntity>?) {
        (historyRecyclerView.adapter as HistoryAdapter).histories = t
    }
}