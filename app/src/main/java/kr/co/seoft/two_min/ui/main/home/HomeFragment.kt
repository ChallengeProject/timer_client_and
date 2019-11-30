package kr.co.seoft.two_min.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.main.MainActivity

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    val updater by lazy {
        HomeFragUpdater(this)
    }

    val numberBtns by lazy {
        arrayOf(
            fragHomeTv0,
            fragHomeTv1,
            fragHomeTv2,
            fragHomeTv3,
            fragHomeTv4,
            fragHomeTv5,
            fragHomeTv6,
            fragHomeTv7,
            fragHomeTv8,
            fragHomeTv9
        )
    }

    val act by lazy {
        activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragHomeViewTransparent.setOnClickListener { /*pass*/ }

        fragHomeTv1.setOnClickListener {
            updater.showTimeInfo(0)
        }
        fragHomeTv2.setOnClickListener {
        }
        fragHomeTv3.setOnClickListener { updater.setBottomLineClearToAllBellType() }
        fragHomeTv4.setOnClickListener { updater.setBottomLineToVibrate() }
        fragHomeTv5.setOnClickListener { updater.setBottomLineToSlient() }
        fragHomeTv6.setOnClickListener {
            act.setShowBottomButtons(true)
        }
        fragHomeTv7.setOnClickListener {
            act.setShowBottomButtons(false)
        }
        fragHomeIvCloseTimeInfo.setOnClickListener {
            updater.hideTimeInfo()
        }


//        fragHomeTvTime
//
//        fragHomeIvClear.setOnClickListener { "fragHomeIvClear.setOnClickListener ".e() }
//        fragHomeTvEtc
//        fragHomeLlNumberPad
//
//        fragHomeTvCancel
//        fragHomeTvBack
//        fragHomeTvHour
//        fragHomeTvMinute
//        fragHomeTvSecond
//
//        fragHomeRv
//        fragHomeViewTransparent
//        fragHomeLlTimeInfo
//        fragHomeEtContent
//        fragHomeTvContentLength
//
//        fragHomeTvSilent
//        fragHomeTvVibrate
//        fragHomeTvBasicBell
//        fragHomeTvSetWhole
//
//        fragHomeTvTimerPosition
//        fragHomeIvTimeDelete
//
//        fragHomeTvCloseTimeInfo


        fragHomeRv.onBadgeSelectedListener = { type, pos ->
            if (type == HomeBadgeCallbackType.ADD_PUSH) {
                fragHomeRv.addHomeBadge(
                    HomeBadge(
                        second = 11,
                        type = HomeBadgeType.NORMAL
                    )
                )
            }
        }
    }

}

