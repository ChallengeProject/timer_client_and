package kr.co.seoft.two_min.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelperForFrag
import kr.co.seoft.two_min.util.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        const val MAX_SECOND = 360000
    }

    var mainSecond = 0L
    var subSecond = ""
    var isRepeatOn = false

    val updater by lazy {
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
        activity as ActivityHelperForFrag
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
        checkViewVisibleAndSet()
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

        updater.setEnable(fragHomeTvHour, sumSecond(hour = curSumSecond) + sumSecond(hour = 1) <= MAX_SECOND - mainSecond)
        updater.setEnable(fragHomeTvMinute, sumSecond(minute = curSumSecond) + sumSecond(minute = 1) <= MAX_SECOND - mainSecond)

        if (curSumSecond > MAX_SECOND - mainSecond) subSecond = (MAX_SECOND - mainSecond - 1).toString()
        else subSecond = curMixedStr

        fragHomeTvSub.text = "+$subSecond"

        checkViewVisibleAndSet()
    }

    private fun sumSecond(hour: Long = 0, minute: Long = 0, second: Long = 0) = hour * 60 * 60 + minute * 60 + second

    private fun addMainSecond(sec: Long) {
        mainSecond += sec
        updater.setMainTextAndEtc(mainSecond)
        fragHomeRv.setSecond(mainSecond.toInt())
        subSecond = ""
        updateWholeAndRemainTime()
        updater.showRv()
    }

    private fun updateWholeAndRemainTime() {
        val wholeSumSecond = fragHomeRv.getBadges()
            .asSequence()
            .filter { it.type == HomeBadgeType.NORMAL || it.type == HomeBadgeType.FOCUS }
            .sumBy { it.time.seconds }

        updater.setSubText(wholeSumSecond.toFormattingString(), wholeSumSecond.toEndTimeStrAfterSec())
    }

    private fun initListener() {
        fragHomeViewTransparent.setOnClickListener { /*pass*/ }

        fragHomeIncSelectedHomeBadge.setOnClickListener {
            fragHomeRv.setComment(fragHomeEtContent.text.toString())
            updater.hideTimeInfo()
            KeyboardUtil.hideSoftKeyBoard(act, fragHomeEtContent)
        }

        numberBtns.forEach { tv ->
            tv.setOnClickListener {
                pushedNumber(tv.text.toString().toInt())
            }
        }

        fragHomeTvCancel.setOnClickListener {
            act.showSelectorDialog("설정한 테임셋이 초기화돼요. 정말 초기화 하시겠어요?", "취소", "초기화",
                {
                    // none
                }, {
                    resetAll(true)
                })
        }

        fragHomeIvBack.setOnClickListener {
            if (subSecond.isEmpty()) return@setOnClickListener
            pushedNumber(0, true)
            checkViewVisibleAndSet()
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
            fragHomeRv.setSecond(mainSecond.toInt())
            updateWholeAndRemainTime()

            checkViewVisibleAndSet()
        }

        fragHomeRv.onBadgeSelectedListener = { type, pos ->

            when (type) {
                HomeBadgeCallbackType.ADD_PUSH -> {
                    fragHomeRv.addHomeBadge()

                    resetMainAndSubSecond()
                    updater.setMainTextAndEtc(mainSecond)
                    updateWholeAndRemainTime()

                    checkViewVisibleAndSet()
                }
                HomeBadgeCallbackType.NORMAL_PUSH -> {
                    resetMainAndSubSecond(fragHomeRv.getBadge(pos).time.seconds.toLong())
                    updater.setMainTextAndEtc(mainSecond)
                    updateWholeAndRemainTime()

                    /**
                     * NORMAL_PUSH 상황이라서 ADD 작업은 포커싱해제타이밍이며 마지막 뱃지말고 처음~중간 부분 뱃지도 작업이 끝난상황이라서
                     * 00:00:00 인애들을 걸러내도됨
                     */
                    fragHomeRv.removeZeroSecondBadge()
                    checkViewVisibleAndSet()
                }
                HomeBadgeCallbackType.FOCUS_PUSH -> {
                    updater.showTimeInfo(pos, fragHomeRv.getBadges())
                }

                HomeBadgeCallbackType.REPEAT_ON_PUSH -> {
                    fragHomeRv.setRepeatBadgeStatus(false)
                    isRepeatOn = false
                }

                HomeBadgeCallbackType.REPEAT_OFF_PUSH -> {
                    fragHomeRv.setRepeatBadgeStatus(true)
                    isRepeatOn = true
                }
            }
        }

        EditTextLengthExceedCheckUtil.checkAndBlockExceed(
            fragHomeEtContent,
            fragHomeTvContentLength,
            100,
            R.color.ux_black,
            R.color.ux_pink
        )

        fragHomeTvSilent.setOnClickListener {
            updater.setBottomLineClearToAllBellType()
            updater.setBottomLineToSlient()
            fragHomeRv.setBellType(Bell.Type.SLIENT)
        }

        fragHomeTvVibrate.setOnClickListener {
            updater.setBottomLineClearToAllBellType()
            updater.setBottomLineToVibrate()
            fragHomeRv.setBellType(Bell.Type.VIBRATION)
        }

        fragHomeTvBasicBell.setOnClickListener {
            updater.setBottomLineClearToAllBellType()
            updater.setBottomLineToBasicBell()
            fragHomeRv.setBellType(Bell.Type.DEFAULT)
        }

        fragHomeTvSetWhole.setOnClickListener {
            val bellType = fragHomeRv.setCurPosBellTypeToWhole()
            act.showToastMessage(
                "모든 타이머의 사운드가 ${bellType.bellTypeToString()}으로 지정됐어요!"
            )
        }

        fragHomeIvTimeSetDelete.setOnClickListener {

            fragHomeRv.removeFoucsingBadge()
            updater.hideTimeInfo()

            resetMainAndSubSecond(fragHomeRv.getFocusingBadge().time.seconds.toLong())

            updater.setMainTextAndEtc(mainSecond)
            updater.setSubText(subSecond)
            updateWholeAndRemainTime()

            checkViewVisibleAndSet()
        }
    }

    private fun resetMainAndSubSecond(newMainSecond: Long = 0L) {
        mainSecond = newMainSecond
        subSecond = ""
    }

    private fun checkViewVisibleAndSet() {

        if (fragHomeRv.getBadges().size <= 3) {
            if (mainSecond == 0L) {
                updater.hideRv()
            } else {
                updater.showRv()
            }
        }

        if (subSecond == "") {
            updater.hideControlButton()
        } else {
            updater.showControlButton()
        }

    }

    fun requestProc() {

        val timeSet = TimeSet(
            title = "무제 타임셋",
            times = fragHomeRv.getBadges()
                .asSequence()
                .filter { it.type == HomeBadgeType.NORMAL || it.type == HomeBadgeType.FOCUS }
                .map { it.time }
                .toList(),
            memo = "",
            timeSetId = 0
        )
        resetAll()
        act.startProc(timeSet, isRepeatOn)
    }

    fun requestSave() {

        val timeSet = TimeSet(

            title = "무제 타임셋",
            times = fragHomeRv.getBadges()
                .asSequence()
                .filter { (it.type == HomeBadgeType.NORMAL || it.type == HomeBadgeType.FOCUS) && it.time.seconds != 0 }
                .map { it.time }
                .toList(),
            memo = Preferencer.getCurrentMemo(act.baseContext),
            timeSetId = 0
        )
        resetAll()
        act.startSave(timeSet)
    }

    fun resetAll(initRepeat: Boolean = false) {
        resetMainAndSubSecond()
        updater.setMainTextAndEtc(mainSecond)
        updater.setSubText(subSecond)
        fragHomeRv.resetBadges()
        checkViewVisibleAndSet()

        if (initRepeat) isRepeatOn = false
    }

    fun loadTimeSet(timeSet: TimeSet) {
        updater.showRv()
        val firstTimeSecond = timeSet.times.first().seconds
        mainSecond = firstTimeSecond.toLong()
        fragHomeRv.initPositionAndFocusAndFirstValueForLoadTimeSets(firstTimeSecond)
        updater.setMainTextAndEtc(firstTimeSecond.toLong(), true)

        fragHomeRv.addHomeBadges(
            timeSet.times.drop(1).map {
                HomeBadge(it, HomeBadgeType.NORMAL)
            }.toList()
        )
    }

}

