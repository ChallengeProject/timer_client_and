package com.timer.toolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.toolbar.`interface`.IToolbarClickListener
import kotlinx.android.synthetic.main.fragment_toolbar.*

class ToolbarFragment : Fragment(), View.OnClickListener {
    var clickListener: IToolbarClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_toolbar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeButton(backBtn, ToolbarButtonType.BACK)
        initializeButton(deleteBtn, ToolbarButtonType.DELETE)
        initializeButton(shareBtn, ToolbarButtonType.SHARE)
        initializeButton(homeBtn, ToolbarButtonType.HOME)
        initializeButton(pac_manBtn, ToolbarButtonType.PAC_MAN)
        initializeButton(wishBtn, ToolbarButtonType.WISH)
        initializeButton(settingsBtn, ToolbarButtonType.SETTINGS)
    }

    private fun initializeButton(button: View, tag: ToolbarButtonType) {
        button.tag = tag
        button.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        clickListener?.onClicked(view.tag as ToolbarButtonType)
    }

    public fun setClickListener() {

    }

    private fun hideAll() {
        val gone = View.GONE
        backBtn.visibility = gone
        deleteBtn.visibility = gone
        shareBtn.visibility = gone
        backBtn.visibility = gone
        homeBtn.visibility = gone
        pac_manBtn.visibility = gone
        wishBtn.visibility = gone
        settingsBtn.visibility = gone
        title.visibility = gone
        version.visibility = gone
    }
}