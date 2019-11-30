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
import kr.co.seoft.two_min.util.toEndTimeStrAfterSec
import kr.co.seoft.two_min.util.toFormattingString

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()

        const val MAX_SECOND = 360000

    }

    val vm by lazy {
        ViewModelProviders.of(this, HomeViewModel(act.db).create())
            .get(HomeViewModel::class.java)
    }

    var mainSecond = 0L
    var subSecond = ""

    private val updater by lazy {
        HomeFragUpdater(this)
    }

    private val numberBtns by lazy {
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

    private fun initView() {
        updater.setMainTextAndEtc(mainSecond)
    }

    private fun initObservable() {


    }

    private fun pushedNumber(num: Int, isBackKey: Boolean = false) {
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

        updater.setEnable(fragHomeTvHour, sumSecond(hour = curSumSecond) + sumSecond(hour = 1) <= MAX_SECOND - mainSecond)
        updater.setEnable(fragHomeTvMinute, sumSecond(minute = curSumSecond) + sumSecond(minute = 1) <= MAX_SECOND - mainSecond)

        if (curSumSecond > MAX_SECOND - mainSecond) subSecond = (MAX_SECOND - mainSecond - 1).toString()
        else subSecond = curMixedStr

        fragHomeTvSub.text = "+$subSecond"
    }

    private fun sumSecond(hour: Long = 0, minute: Long = 0, second: Long = 0) = hour * 60 * 60 + minute * 60 + second

    private fun addMainSecond(sec: Long) {
        mainSecond += sec
        updater.setMainTextAndEtc(mainSecond)
        fragHomeRv.setFocusingBadge(fragHomeRv.getFocusingBadge().copy(second = mainSecond.toInt()))
        subSecond = ""
        updateWholeAndRemainTime()
    }

    private fun updateWholeAndRemainTime() {
        val wholeSumSecond = fragHomeRv.getBadges()
            .asSequence()
            .filter { it.type == HomeBadgeType.NORMAL || it.type == HomeBadgeType.FOCUS }
            .sumBy { it.second }

        updater.setSubText(wholeSumSecond.toFormattingString(), wholeSumSecond.toEndTimeStrAfterSec())
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
            // TODO 다이얼로그로 해야됨

            resetMainAndSubSecond()
            updater.setMainTextAndEtc(mainSecond)
            updater.setSubText(subSecond)
            fragHomeRv.resetBadges()
        }

        fragHomeIvBack.setOnClickListener {
            if (subSecond.isEmpty()) return@setOnClickListener
            pushedNumber(0, true)
        }

        fragHomeTvHour.setOnClickListener {
            if (subSecond.isEmpty()) return@setOnClickListener
            addMainSecond(sumSecond(hour = subSecond.toLong()))
        }

        fragHomeTvMinute.setOnClickListener {
            if (subSecond.isEmpty()) return@setOnClickListener
            addMainSecond(sumSecond(minute = subSecond.toLong()))
        }

        fragHomeTvSecond.setOnClickListener {
            if (subSecond.isEmpty()) return@setOnClickListener
            addMainSecond(subSecond.toLong())
        }

        fragHomeIvMainClear.setOnClickListener {
            resetMainAndSubSecond()
            updater.setMainTextAndEtc(mainSecond)
            fragHomeRv.setFocusingBadge(fragHomeRv.getFocusingBadge().copy(second = mainSecond.toInt()))
            updateWholeAndRemainTime()
        }

        fragHomeRv.onBadgeSelectedListener = { type, pos ->
            if (type == HomeBadgeCallbackType.ADD_PUSH) {
                fragHomeRv.addHomeBadge()

                resetMainAndSubSecond()
                updater.setMainTextAndEtc(mainSecond)
                updateWholeAndRemainTime()

            } else if (type == HomeBadgeCallbackType.NORMAL_PUSH) {
                resetMainAndSubSecond(fragHomeRv.getBadge(pos).second.toLong())
                updater.setMainTextAndEtc(mainSecond)
                updateWholeAndRemainTime()

                /**
                 * NORMAL_PUSH 상황이라서 ADD 작업은 포커싱해제타이밍이며 마지막 뱃지말고 처음~중간 부분 뱃지도 작업이 끝난상황이라서
                 * 00:00:00 인애들을 걸러내도됨
                 */
                fragHomeRv.removeZeroSecondBadge()

            } else if (type == HomeBadgeCallbackType.FOCUS_PUSH) {

//                updater.showTimeInfo(pos,fragHomeRv.getBadge(pos))

                "HomeBadgeCallbackType.FOCUS_PUSH".e()
            }
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


    }

    private fun resetMainAndSubSecond(newMainSecond: Long = 0L) {
        mainSecond = newMainSecond
        subSecond = ""
    }

}

