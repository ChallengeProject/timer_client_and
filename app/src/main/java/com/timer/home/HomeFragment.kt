package com.timer.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.main.MainActivity
import com.timer.toolbar.ToolbarFragment.Initializer
import com.timer.toolbar.model.ToolbarButtonType
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

class HomeFragment : Fragment() {
    private val keys = arrayListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "<", "시", "분", "초")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false) as ViewGroup
        view.container.gridLayout {
            layoutParams = ViewGroup.LayoutParams(wrapContent, wrapContent)
            columnCount = 3
            keys.forEach { text ->
                button(text) {
                    tag = text
                    width = dip(50)
                    height = dip(65)
                    textSize = 30F
                    backgroundColor = Color.WHITE
                    onClick { toast("clicked $text") }
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        (activity as MainActivity).mainViewModel.setToolbarInitializer(
            initializer = Initializer().enableButton(ToolbarButtonType.Search) {
                // Search button click listener
            }.enableButton(ToolbarButtonType.History) {
                // History button click listener
            }.enableButton(ToolbarButtonType.Settings) {
                // Settings button click listener
            })

    }
}