package com.timer.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class HomeTabPagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>) :
    FragmentPagerAdapter(fm) {
    companion object {
        const val HOME_TABS_COUNT = 3
    }

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getItemPosition(`object`: Any): Int {
        return fragments.indexOf(`object`)
    }

    override fun getCount(): Int {
        return HOME_TABS_COUNT
    }
}