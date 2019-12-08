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
import gun0912.tedkeyboardobserver.BaseKeyboardObserver
import gun0912.tedkeyboardobserver.TedKeyboardObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelperForFrag
import kr.co.seoft.two_min.ui.main.home.HomeFragment
import kr.co.seoft.two_min.ui.main.mytimeset.MyTimeSetFragment
import kr.co.seoft.two_min.ui.preview.PreviewActivity
import kr.co.seoft.two_min.ui.proc.ProcActivity
import kr.co.seoft.two_min.ui.proc.ProcEndActivity
import kr.co.seoft.two_min.ui.proc.ProcExceedActivity
import kr.co.seoft.two_min.ui.save.SaveActivity
import kr.co.seoft.two_min.util.*


class MainActivity : ActivityHelperForFrag() {

    companion object {
        const val PROC_ACTIVITY = 1111
        const val PROC_END_ACTIVITY = 1112
        const val PROC_EXCEED_ACTIVITY = 1113
        const val SAVE_ACTIVITY = 1114
        const val PREVIEW_ACTIVITY = 1115
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

        TedKeyboardObserver(this).listen(
            object : BaseKeyboardObserver.OnKeyboardListener {
                override fun onKeyboardChange(isShow: Boolean) {
                    if (isShow) {
                        setShowTabLayout(false)
                        actMainLlBottomButtons.visibility = View.INVISIBLE
                    } else {
                        setShowTabLayout(true)
                        if (showStatusBottomButtons) actMainLlBottomButtons.visibility = View.VISIBLE
                    }
                }
            })
    }

    override fun setLockViewpager(isLock: Boolean) {
        actMainViewPager.isUserInputEnabled = !isLock
    }

    override fun setShowBottomButtons(isShow: Boolean) {
        showStatusBottomButtons = isShow
        actMainLlBottomButtons.visibility = isShow.setVisibleOrGone()
    }

    override fun startProc(timeSet: TimeSet) {
        ProcActivity.startProcActivity(this, timeSet, 5)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        "onActivityResult111".i()

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PROC_ACTIVITY -> {
                    "PROC_ACTIVITY in Main".i()
                    ProcEndActivity.startProcEndActivity(
                        this,
                        data.getParcelableExtra(ProcActivity.RESP_TIME_SET),
                        data.getParcelableExtra(ProcActivity.RESP_USE_INFO)
                    )
                }
                PROC_END_ACTIVITY -> {
                    "PROC_END_ACTIVITY in Main".i()
                    val timeSet = data.getParcelableExtra<TimeSet>(ProcEndActivity.RESP_TIME_SET)
                    when (data.getStringExtra(ProcEndActivity.RESP_TYPE)) {

                        ProcEndActivity.RESP_TYPE_EXCEED -> {
                            ProcExceedActivity.startProcExceedActivity(this, timeSet, 3)
                        }
                        ProcEndActivity.RESP_TYPE_RESTART -> {
                            ProcActivity.startProcActivity(this, timeSet, 3)
                        }
                        ProcEndActivity.RESP_TYPE_SAVE -> {
                            // TODO 저장 구체화되면 ㄱㄱ
                        }
                    }
                }
                PROC_EXCEED_ACTIVITY -> {

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

    fun movePage(page:Int){
        actMainViewPager.setCurrentItem(page,true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.main_home_setting -> {
                "main_home_setting".toaste(this)


            }
            R.id.main_home_history -> {
                "main_home_history".toaste(this)


//                actMainViewPager.isUserInputEnabled =false
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
