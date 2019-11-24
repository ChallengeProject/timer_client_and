package com.timer.hometab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.history.HistoryListFragment
import com.timer.hometab.home.HomeFragment
import com.timer.hometab.timeset.local.MyTimeSetListFragment
import com.timer.hometab.timeset.remote.SharedTimeSetListFragment
import com.timer.main.HomeTabPagerAdapter
import com.timer.main.MainActivity
import com.timer.toolbar.ToolbarFragment
import com.timer.toolbar.model.ToolbarButtonType
import kotlinx.android.synthetic.main.fragment_tab.*

class HomeTabFragment : Fragment() {
    private val homeFragment = HomeFragment()
    private val myTimeSetListFragment = MyTimeSetListFragment()
    private val sharedTimeSetListFragment = SharedTimeSetListFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initTabViewPager()
    }

    private fun initToolbar() {
        (activity as MainActivity).mainViewModel.setToolbarInitializer(
            initializer = ToolbarFragment.Initializer().enableButton(ToolbarButtonType.Search) {
                // Search button click listener
            }.enableButton(ToolbarButtonType.History) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.contentFragment, HistoryListFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }.enableButton(ToolbarButtonType.Settings) {
                // Settings button click listener
            })
    }

    private fun initTabViewPager() {
        tabViewPager.apply {
            adapter = HomeTabPagerAdapter(fragmentManager, listOf(myTimeSetListFragment, homeFragment, sharedTimeSetListFragment))
                .apply { post { tabViewPager.currentItem = getItemPosition(homeFragment) } }
        }
    }
}