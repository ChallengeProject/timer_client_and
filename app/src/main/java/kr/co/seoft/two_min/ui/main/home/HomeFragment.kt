package kr.co.seoft.two_min.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.e
import kr.co.seoft.two_min.util.toFormattingString

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()

        val MAX_SECOND = 360000

    }

    val vm by lazy {
        ViewModelProviders.of(this, HomeViewModel(act.db).create())
            .get(HomeViewModel::class.java)
    }

    var mainSecond = 0
    var subSecond = ""

    fun pushedNumber(num: Int, isBackKey: Boolean = false) {
        if (!isBackKey && num == 0 && subSecond.isEmpty()) return
        if (subSecond.length <= 1 && isBackKey) {
            subSecond = ""
            fragHomeTvSub.text = ""
            return
        }
        val tmpStr = if (subSecond.isEmpty()) "" else subSecond
        val curMixedStr = if (isBackKey) subSecond.substring(0, subSecond.length - 1) else "$tmpStr$num"
        val curSumSecond = curMixedStr.toLong()
        "curSumSecond $curSumSecond".e()

        if (sumSecond(hour = curSumSecond) + sumSecond(hour = 1) > MAX_SECOND) {
            updater.setEnable(fragHomeTvHour, false)
        } else {
            updater.setEnable(fragHomeTvHour, true)
        }

        if (sumSecond(minute = curSumSecond) + sumSecond(minute = 1) > MAX_SECOND) {
            updater.setEnable(fragHomeTvMinute, false)
        } else {
            updater.setEnable(fragHomeTvMinute, true)
        }

        if (curSumSecond > MAX_SECOND) {
            subSecond = (MAX_SECOND - mainSecond - 1).toString()
        } else {
            subSecond = curMixedStr
        }

        fragHomeTvSub.text = "+$subSecond"
    }

    fun sumSecond(hour: Long = 0, minute: Long = 0, second: Long = 0) = hour * 60 * 60 + minute * 60 + second

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

        initObservable()
        initListener()
        initView()
    }

    fun initView() {
        fragHomeTvMain.text = mainSecond.toFormattingString()

    }

    private fun initObservable() {


    }

    fun setResultSecond(sec: Int) {

        fragHomeTvMain.text = sec.toFormattingString()
        fragHomeRv

    }

    private fun initListener() {
        fragHomeViewTransparent.setOnClickListener { /*pass*/ }

        fragHomeIvCloseTimeInfo.setOnClickListener {
            updater.hideTimeInfo()
        }

        numberBtns.forEach { tv ->
            tv.setOnClickListener {
                pushedNumber(tv.text.toString().toInt())
            }
        }

        fragHomeTvCancel.setOnClickListener {
            // TODO
        }

        fragHomeIvBack.setOnClickListener {
            if (subSecond.isEmpty()) return@setOnClickListener
            pushedNumber(0, true)
        }

        fragHomeTvHour.setOnClickListener {
            setResultSecond(sumSecond(hour = subSecond.toLong(), second = mainSecond.toLong()).toInt())
        }

        fragHomeTvMinute.setOnClickListener {
            setResultSecond(sumSecond(minute = subSecond.toLong(), second = mainSecond.toLong()).toInt())
        }

        fragHomeTvSecond.setOnClickListener {
            setResultSecond(mainSecond + subSecond.toInt())
        }

//        fragHomeTvTime
//
//        fragHomeIvClear.setOnClickListener { "fragHomeIvClear.setOnClickListener ".e() }
//        fragHomeTvEtc
//        fragHomeLlNumberPad
//

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

