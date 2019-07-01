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
import com.timer.timeset.local.LocalTimeSetListFragment
import com.timer.timeset.remote.SharedTimeSetListFragment
import com.timer.toolbar.ToolbarButtonType
import com.timer.toolbar.ToolbarFragment
import com.timer.toolbar.`interface`.IToolbarClickListener
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {
    val toolbar by lazy {
        childFragmentManager.findFragmentById(R.id.toolbar) as ToolbarFragment
    }

    private val homeFragment = HomeFragment()
    private val localTimeSetListFragment = LocalTimeSetListFragment()
    private val sharedTimeSetListFragment = SharedTimeSetListFragment()
    private val settingsFragment = SettingsFragment()
    private val toolbarClickListener = object : IToolbarClickListener {
        override fun onClicked(type: ToolbarButtonType?) {
            println("toolbarClickListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        bottomNavigation.selectedItemId = R.id.action_home
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> changeFragment(homeFragment)
            R.id.action_local_time_set -> changeFragment(localTimeSetListFragment)
            R.id.action_shared_time_set -> changeFragment(sharedTimeSetListFragment)
        }

        return true
    }

    private fun switchContents(type: ToolbarButtonType) {
        when (type) {
            ToolbarButtonType.SETTINGS -> changeFragmentWithFootstep(settingsFragment)
            ToolbarButtonType.HOME -> changeFragment(homeFragment)
//            R.id.timeSetDetailBtn ->
            else -> {
            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.let {
            it.replace(R.id.contents, fragment)
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