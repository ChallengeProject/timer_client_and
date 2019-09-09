package com.timer.toolbar.model

sealed class ToolbarElement

object ToolbarTitle : ToolbarElement()

object ToolbarAdditionalInfo : ToolbarElement()

sealed class ToolbarButtonType : ToolbarElement() {
    object Back : ToolbarButtonType()
    object Cancel : ToolbarButtonType()
    object Delete : ToolbarButtonType()
    object Search : ToolbarButtonType()
    object Share : ToolbarButtonType()
    object Home : ToolbarButtonType()
    object History : ToolbarButtonType()
    object Like : ToolbarButtonType()
    object Settings : ToolbarButtonType()
    object Confirm : ToolbarButtonType()
    object SearchCancel : ToolbarButtonType()
}