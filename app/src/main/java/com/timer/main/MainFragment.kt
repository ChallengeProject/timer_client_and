package com.timer.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.hometab.HomeTabFragment
import com.timer.toolbar.ToolbarFragment

class MainFragment : Fragment(), MainView {
    private val toolbarFragment = ToolbarFragment()
    private val homeTabFragment = HomeTabFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeFragment(R.id.toolbar, toolbarFragment)
        changeFragment(R.id.contentFragment, homeTabFragment)
    }

    override fun setToolbarInitializer(initializer: ToolbarFragment.Initializer) {
        initializer.init(toolbarFragment)
    }

    private fun changeFragment(containerId: Int, fragment: Fragment) {
        fragmentManager?.beginTransaction()?.let {
            it.replace(containerId, fragment)
            it.commit()
        }
    }
}