package com.timer.main

import com.timer.toolbar.ToolbarFragment

interface MainView {
    fun setToolbarInitializer(initializer: ToolbarFragment.Initializer)
}