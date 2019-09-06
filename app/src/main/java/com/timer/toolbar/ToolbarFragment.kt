package com.timer.toolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.databinding.FragmentToolbarBinding
import com.timer.toolbar.model.ToolbarButtonType
import kotlinx.android.synthetic.main.fragment_toolbar.*

class ToolbarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentToolbarBinding>(
            inflater, R.layout.fragment_toolbar, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideAll()
    }

    fun showBtn(vararg buttonType: ToolbarButtonType) {
        hideAll()
        buttonType.forEach { type ->
            when (type) {

            }
        }
    }
//
//    private fun showBtnsTypeHome() {
//        hideAll()
//        searchBtn.visibility = View.VISIBLE
//        historyBtn.visibility = View.VISIBLE
//        settingsBtn.visibility = View.VISIBLE
//        // show(ENUM, ENUM, ENUM, ENUM)으로 변경
//    }
//
//    private fun showBtnsTypeTimeSet() {
//        hideAll()
//        shareBtn.visibility = View.VISIBLE
//        likeBtn.visibility = View.VISIBLE
//        homeBtn.visibility = View.VISIBLE
//    }
//
//    private fun showBtnsTypeTimeSetEdit() {
//        hideAll()
//        cancelBtn.visibility = View.VISIBLE
//        title.visibility = View.VISIBLE
//        confirmBtn.visibility = View.VISIBLE
//    }
//
//    private fun showBtnsTypeSearch() {
//        hideAll()
//        searchArea.visibility = View.VISIBLE
//    }

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
}