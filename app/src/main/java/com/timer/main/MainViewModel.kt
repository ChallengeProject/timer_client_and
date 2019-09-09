package com.timer.main

import com.timer.toolbar.ToolbarFragment

class MainViewModel(private val mainView: MainView) {

    fun setToolbarInitializer(initializer: ToolbarFragment.Initializer) {
        mainView.setToolbarInitializer(initializer)
    }
}