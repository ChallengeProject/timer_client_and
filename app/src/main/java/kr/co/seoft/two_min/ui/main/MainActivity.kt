package kr.co.seoft.two_min.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.History
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.data.UseInfo
import kr.co.seoft.two_min.ui.ActivityHelperForFrag
import kr.co.seoft.two_min.ui.history.HistoriesActivity
import kr.co.seoft.two_min.ui.history.HistoryActivity
import kr.co.seoft.two_min.ui.main.home.HomeFragment
import kr.co.seoft.two_min.ui.main.mytimeset.MyTimeSetFragment
import kr.co.seoft.two_min.ui.preview.PreviewActivity
import kr.co.seoft.two_min.ui.proc.ProcActivity
import kr.co.seoft.two_min.ui.proc.ProcEndActivity
import kr.co.seoft.two_min.ui.proc.ProcExceedActivity
import kr.co.seoft.two_min.ui.save.SaveActivity
import kr.co.seoft.two_min.ui.setting.SettingActivity
import kr.co.seoft.two_min.util.*


class MainActivity : ActivityHelperForFrag() {

    companion object {
        const val PROC_ACTIVITY = 1111
        const val PROC_END_ACTIVITY = 1112
        const val PROC_EXCEED_ACTIVITY = 1113
        const val SAVE_ACTIVITY = 1114
        const val PREVIEW_ACTIVITY = 1115
        const val HISTORIES_ACTIVITY = 1116
    }

    override val layoutResourceId = R.layout.activity_main

    var showStatusBottomButtons = false

    val homeFragment by lazy {
        HomeFragment.newInstance()
    }

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initView()
        initListener()

    }

    override fun onResume() {
        super.onResume()

        when (ProcActivity.respEndType) {
            ProcActivity.RESP_END_TYPE_STOP -> {
                "RESP_END_TYPE_STOP".e()
                val timeSet = ProcActivity.respTimeSet
                val useInfo = ProcActivity.respUseInfo
                saveTimeSetForHistory(timeSet, useInfo)
            }
            ProcActivity.RESP_END_TYPE_END -> {
                "RESP_END_TYPE_END".e()
                ProcEndActivity.startProcEndActivity(
                    this,
                    ProcActivity.respTimeSet,
                    ProcActivity.respUseInfo
                )
            }
        }
        ProcActivity.respEndType = ProcActivity.RESP_END_TYPE_NONE
    }

    private fun initView() {

        actMainViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        homeFragment
                    }
                    else -> {
                        MyTimeSetFragment.newInstance()
                    }
                }
            }
        }

        actMainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    toolbar.title = ""
                    actMainViewTabLayoutLine1.visibility = View.VISIBLE
                    actMainViewTabLayoutLine2.visibility = View.INVISIBLE
                } else {
                    toolbar.title = "내 타임셋"
                    actMainViewTabLayoutLine1.visibility = View.INVISIBLE
                    actMainViewTabLayoutLine2.visibility = View.VISIBLE
                }
            }
        })

        actMainTablayout.getTabAt(0)?.setIcon(R.drawable.dummy)
        actMainTablayout.getTabAt(1)?.setIcon(R.drawable.dummy)

        TabLayoutMediator(actMainTablayout, actMainViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Size"
                    tab.setIcon(R.drawable.dummy)
                }
                else -> {
                    tab.text = "Template"
                    tab.setIcon(R.drawable.btn_back)
                }
            }
        }.attach()

    }

    fun initListener() {
        actMainBtBottom1Btn.setOnClickListener {
            homeFragment.requestSave()
        }

        actMainBtBottom2Btn.setOnClickListener {
            homeFragment.requestProc()
        }
        actMainViewTransparentTop.setOnClickListener { /*pass*/ }
        actMainViewTransparentBottom.setOnClickListener { /*pass*/ }

        // manifest에 adjustResize|adjustPan 같은 옵션을주어 동작하지않음, 근대 해당옵션으로 원하는대로 동작함 별일없으면 주석처리
//        TedKeyboardObserver(this).listen(
//            object : BaseKeyboardObserver.OnKeyboardListener {
//                override fun onKeyboardChange(isShow: Boolean) {
//                    if (isShow) {
//                        setShowTabLayout(false)
//                        actMainLlBottomButtons.visibility = View.INVISIBLE
//                    } else {
//                        setShowTabLayout(true)
//                        if (showStatusBottomButtons) actMainLlBottomButtons.visibility = View.VISIBLE
//                    }
//                }
//            })
    }

    override fun setLockViewpager(isLock: Boolean) {
        actMainViewPager.isUserInputEnabled = !isLock
    }

    override fun setShowBottomButtons(isShow: Boolean) {
        showStatusBottomButtons = isShow
        actMainLlBottomButtons.visibility = isShow.setVisibleOrGone()
    }

    override fun startProc(timeSet: TimeSet) {
        ProcActivity.startProcActivity(this, timeSet, 3)
    }

    override fun startSave(timeSet: TimeSet) {
        SaveActivity.startSaveActivity(this, timeSet)
    }

    override fun setTransparentToolbarAndBottoms(isTransparent: Boolean) {
        actMainViewTransparentTop.visibility = isTransparent.setVisible()
        actMainViewTransparentBottom.visibility = isTransparent.setVisible()
    }

    fun setShowTabLayout(isShow: Boolean) {
        actMainLlTabLayoutLine.visibility = isShow.setVisibleOrGone()
        actMainTablayout.visibility = isShow.setVisibleOrGone()
    }

    private fun saveTimeSetForHistory(timeSet: TimeSet, useInfo: UseInfo) {

        compositeDisposable.add(
            db.timeSetDao().insertTimeSet(
                timeSet.copy().apply {
                    timeSetId = 0
                    useHistory = 1
                }
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    saveHistory(timeSet.apply { timeSetId = it }, useInfo)
                }, {
                    it.printStackTrace()
                })

        )
    }

    fun saveHistory(timeSet: TimeSet, useInfo: UseInfo) {
        compositeDisposable.add(
            db.timeSetDao().insertHistory(
                History(
                    timeSetId = timeSet.timeSetId,
                    wholeTime = timeSet.wholeTime,
                    timeSetTitle = timeSet.title,
                    startTimeString = useInfo.startTimeString,
                    endTimeString = useInfo.endTimeString,
                    addMinute = useInfo.addMinute,
                    repeatCount = useInfo.repeatCount,
                    pauseCount = useInfo.pauseCount,
                    changeCount = useInfo.changeCount,
                    cancelInfo = useInfo.cancelInfo,
                    exceedSecond = useInfo.exceedSecond,
                    ymdString = useInfo.ymdString
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    "저장완료todo".e()

                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        "onActivityResult111".i()

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PROC_END_ACTIVITY -> {
                    "PROC_END_ACTIVITY in Main".i()
                    val timeSet = data.getParcelableExtra<TimeSet>(ProcEndActivity.RESP_TIME_SET)
                    val useInfo = data.getParcelableExtra<UseInfo>(ProcEndActivity.RESP_USE_INFO)
                    when (data.getStringExtra(ProcEndActivity.RESP_TYPE)) {

                        ProcEndActivity.RESP_TYPE_EXIT -> {
                            saveTimeSetForHistory(timeSet, useInfo)
                        }
                        ProcEndActivity.RESP_TYPE_EXCEED -> {
                            ProcExceedActivity.startProcExceedActivity(this, timeSet, useInfo)
                        }
                        ProcEndActivity.RESP_TYPE_RESTART -> {
                            ProcActivity.startProcActivity(this, timeSet, 3)
                            saveTimeSetForHistory(timeSet, useInfo)
                        }
                        ProcEndActivity.RESP_TYPE_SAVE -> {
                            saveTimeSetForHistory(timeSet, useInfo)
                            startSave(timeSet)
                        }
                    }
                }
                PROC_EXCEED_ACTIVITY -> {
                    val timeSet = data.getParcelableExtra<TimeSet>(ProcExceedActivity.RESP_TIME_SET)
                    val useInfo = data.getParcelableExtra<UseInfo>(ProcExceedActivity.RESP_USE_INFO)
                    saveTimeSetForHistory(timeSet, useInfo)
                }
                SAVE_ACTIVITY -> {
                    val timeSetId = data.getLongExtra(SaveActivity.RESP_TIME_SET_ID, 0L)
                    startPreviewActivity(timeSetId)
                }
                PREVIEW_ACTIVITY -> {
                    val timeSetId = data.getLongExtra(PreviewActivity.RESP_TIME_SET_ID_FOR_START, 0L)
                    compositeDisposable.add(
                        AppDatabase.getDatabase(this).timeSetDao().getTimeSetById(timeSetId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                startProc(it)
                            }, {
                                it.printStackTrace()
                            })
                    )
                }
                HISTORIES_ACTIVITY -> {
                    val timeSet = data.getParcelableExtra(HistoryActivity.RESP_TIME_SET) as TimeSet
                    val responseType = data.getIntExtra(HistoryActivity.RESP_HISTORY_RESPONSE_TYPE, 0)

                    if (responseType == HistoryActivity.HISTORY_RESP_TYPE_SAVE) {
                        startSave(timeSet.apply {
                            timeSetId = 0L
                        })
                    } else { // HistoryActivity.HISTORY_RESP_TYPE_START
                        startProc(timeSet.apply {
                            timeSetId = 0L
                        })
                    }


                }

            }
        }
    }

    fun startPreviewActivity(timeSetId: Long) {
        PreviewActivity.startSaveActivity(this, timeSetId)
    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(false) // true is back icon
            setHomeAsUpIndicator(R.drawable.btn_back)
        }
    }

    fun movePage(page: Int) {
        actMainViewPager.setCurrentItem(page, true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.main_home_setting -> {
                "main_home_setting".toaste(this)
                SettingActivity.startSettingActivity(this)
            }
            R.id.main_home_history -> {
                "main_home_history".toaste(this)
                HistoriesActivity.startHistoriesActivity(this)
            }
            android.R.id.home -> {
                "android.R.id.home".toaste(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_home, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
