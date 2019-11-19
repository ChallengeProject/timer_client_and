package com.timer.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.home.HomeFragment
import com.timer.settings.SettingsFragment
import com.timer.timeset.local.MyTimeSetListFragment
import com.timer.timeset.remote.SharedTimeSetListFragment
import com.timer.toolbar.ToolbarFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), MainView {

    private val toolbarFragment = ToolbarFragment()
    private val homeFragment = HomeFragment()
    private val myTimeSetListFragment = MyTimeSetListFragment()
    private val sharedTimeSetListFragment = SharedTimeSetListFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeFragment(R.id.toolbar, toolbarFragment)

        homeTabViewPager.apply {
            adapter = HomeTabPagerAdapter(
                fragmentManager,
                listOf(myTimeSetListFragment, homeFragment, sharedTimeSetListFragment)
            )
                .apply {
                    post { homeTabViewPager.currentItem = getItemPosition(homeFragment) }
                }
        }
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