package com.timer.toolbar.`interface`

import com.timer.toolbar.ToolbarButtonType

@FunctionalInterface
interface IToolbarClickListener {
    fun onClicked(type: ToolbarButtonType?)
}