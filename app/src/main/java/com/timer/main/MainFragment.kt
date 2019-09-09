package com.timer.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timer.R
import com.timer.home.HomeFragment
import com.timer.settings.SettingsFragment
import com.timer.timeset.local.MyTimeSetListFragment
import com.timer.timeset.remote.SharedTimeSetListFragment
import com.timer.toolbar.ToolbarFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val toolbarFragment = ToolbarFragment()
    private val homeFragment = HomeFragment()
    private val myTimeSetListFragment = MyTimeSetListFragment()
    private val sharedTimeSetListFragment = SharedTimeSetListFragment()
    private val settingsFragment = SettingsFragment()
//    private val toolbarClickListener = object : IToolbarClickListener {
//        override fun onClicked(type: ToolbarButtonType?) {
//            println("toolbarClickListener")
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        bottomNavigation.selectedItemId = R.id.action_home

        changeFragment(R.id.toolbar, toolbarFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> changeFragment(R.id.contents, homeFragment)
            R.id.action_my_time_set -> changeFragment(R.id.contents, myTimeSetListFragment)
            R.id.action_shared_time_set -> changeFragment(R.id.contents, sharedTimeSetListFragment)
        }

        return true
    }

//    private fun switchContents(type: ToolbarButtonType) {
//        when (type) {
//            ToolbarButtonType.SETTINGS -> changeFragmentWithFootstep(settingsFragment)
//            ToolbarButtonType.HOME -> changeFragment(homeFragment)
////            R.id.timeSetDetailBtn ->
//            else -> {
//            }
//        }
//    }

    private fun changeFragment(containerId: Int, fragment: Fragment) {
        fragmentManager?.beginTransaction()?.let {
            it.replace(containerId, fragment)
            it.commit()
        }
    }

    private fun changeFragmentWithFootstep(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.let {
            it.replace(R.id.contents, fragment)
            it.addToBackStack(null)
            it.commit()
        }
    }
}