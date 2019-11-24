package com.timer.timeset.local

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timer.R
import com.timer.data.AppDatabase
import com.timer.se_util.e

class MyTimeSetListFragment : Fragment() {

    private lateinit var fragHistoryRvContent: RecyclerView
    private val hisotryViewModel by lazy {
        ViewModelProviders.of(
            requireActivity(), HistoryViewModel(
                requireActivity().application,
                AppDatabase.getInstance(requireContext())
            ).create()
        ).get(HistoryViewModel::class.java)
    }

    private val historyAdapter by lazy {
        HistoryAdapter(requireActivity(), hisotryViewModel) {
            HistoryActivity.startActivity(requireContext(), it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_list, null)

        fragHistoryRvContent = view.findViewById(R.id.fragHistoryListRvContent)
        val fragHistoryBtRefresh = view.findViewById<Button>(R.id.fragHistoryListBtRefresh)

        fragHistoryRvContent.adapter = historyAdapter

        historyAdapter.setVM(this)

        fragHistoryRvContent.layoutManager = LinearLayoutManager(requireActivity())

        fragHistoryBtRefresh.setOnClickListener {
            hisotryViewModel.refreshItems()
        }

        hisotryViewModel.observableText.observe(this, Observer {
            it.e()
        })

        return view
    }
}