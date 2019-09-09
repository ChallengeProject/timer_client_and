package com.timer.toolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.databinding.FragmentToolbarBinding
import com.timer.toolbar.model.ToolbarAdditionalInfo
import com.timer.toolbar.model.ToolbarButtonType
import com.timer.toolbar.model.ToolbarElement
import com.timer.toolbar.model.ToolbarTitle
import kotlinx.android.synthetic.main.fragment_toolbar.*

class ToolbarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentToolbarBinding>(
            inflater, R.layout.fragment_toolbar, container, false
        )
        return binding.root
    }

    private fun setTitle(titleStr: String) {
        title.text = titleStr
        title.visibility = View.VISIBLE
    }

    /*
    ** info : version or date
     */
    private fun setAdditionalInfo(info: String) {
        additionalInfo.text = info
        additionalInfo.visibility = View.VISIBLE
    }

    private fun initButton(btn: View, buttonType: ToolbarButtonType, clickListener: (View) -> Unit) {
        btn.tag = buttonType
        btn.setOnClickListener { clickListener.invoke(it) }
        btn.visibility = View.VISIBLE
    }

    private fun getButton(type: ToolbarButtonType): View {
        return when (type) {
            ToolbarButtonType.Back -> backBtn
            ToolbarButtonType.Cancel -> cancelBtn
            ToolbarButtonType.Delete -> deleteBtn
            ToolbarButtonType.Search -> searchBtn
            ToolbarButtonType.Share -> shareBtn
            ToolbarButtonType.Home -> homeBtn
            ToolbarButtonType.History -> historyBtn
            ToolbarButtonType.Like -> likeBtn
            ToolbarButtonType.Settings -> settingsBtn
            ToolbarButtonType.Confirm -> confirmBtn
            ToolbarButtonType.SearchCancel -> searchCancelBtn
        }
    }

    private fun hideAll() {
        val gone = View.GONE

        searchBtn.visibility = gone
        historyBtn.visibility = gone
        settingsBtn.visibility = gone

        shareBtn.visibility = gone
        likeBtn.visibility = gone
        homeBtn.visibility = gone

        title.visibility = gone
        backBtn.visibility = gone
        deleteBtn.visibility = gone
        additionalInfo.visibility = gone
        searchArea.visibility = gone

        cancelBtn.visibility = gone
        confirmBtn.visibility = gone
    }

    class Initializer {
        private val map = HashMap<ToolbarElement, Any>()

        fun enableButton(buttonType: ToolbarButtonType, clickListener: (View) -> Unit): Initializer {
            map[buttonType] = clickListener
            return this
        }

        fun enableTitle(title: String): Initializer {
            map[ToolbarTitle] = title
            return this
        }

        fun enableAdditionalInfo(info: String): Initializer {
            map[ToolbarAdditionalInfo] = info
            return this
        }

        fun init(toolbar: ToolbarFragment) {
            toolbar.hideAll()
            val buttons = map.filter { it.key is ToolbarButtonType }.keys
            buttons.forEach {
                toolbar.initButton(toolbar.getButton(it as ToolbarButtonType), it, map[it] as (View) -> Unit)
            }

            map[ToolbarTitle]?.let { toolbar.setTitle(it as String) }
            map[ToolbarAdditionalInfo]?.let { toolbar.setAdditionalInfo(it as String) }
        }
    }
}