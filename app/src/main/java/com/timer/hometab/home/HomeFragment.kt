package com.timer.hometab.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.timer.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initKeypad()
    }

    private fun initKeypad() {
        val keypadElements =
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "취소", "0", "<", "시", "분", "초")
        keypadContainer.adapter = ArrayAdapter(context!!, R.layout.item_keypad, keypadElements)
    }
}