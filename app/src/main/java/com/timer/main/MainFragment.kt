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
import com.timer.proc.ProcActivity
import com.timer.settings.SettingsFragment
import com.timer.timeset.local.MyTimeSetListFragment
import com.timer.timeset.remote.SharedTimeSetListFragment
import com.timer.toolbar.ToolbarFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), MainView, BottomNavigationView.OnNavigationItemSelectedListener {

    private val toolbarFragment = ToolbarFragment()
    private val homeFragment = HomeFragment()
    private val myTimeSetListFragment = MyTimeSetListFragment()
    private val sharedTimeSetListFragment = SharedTimeSetListFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeFragment(R.id.toolbar, toolbarFragment)

        with(bottomNavigation) {
            setOnNavigationItemSelectedListener(this@MainFragment)
            selectedItemId = R.id.action_home
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> changeFragment(R.id.contents, homeFragment)

            R.id.action_my_time_set -> {


//                //TODO remove codes
                val times = arrayListOf(
                    3,
                    4,
                    5,
//                    8,
                    5
                )

                val readySec = 5

                ProcActivity.startProcActivity(this.activity!!, readySec, times)

                // TODO open commecnt
//                changeFragment(R.id.contents, myTimeSetListFragment)
            }

            R.id.action_shared_time_set -> changeFragment(R.id.contents, sharedTimeSetListFragment)
        }

        return true
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