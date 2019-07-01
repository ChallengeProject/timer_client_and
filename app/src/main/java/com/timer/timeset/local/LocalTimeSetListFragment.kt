package com.timer.timeset.local

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class LocalTimeSetListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            verticalLayout {
                textView("LocalTimeSetListFragment")
            }
        }.view
    }
}