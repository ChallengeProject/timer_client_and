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
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.main.home.HomeFragment
import kr.co.seoft.two_min.ui.main.mytimeset.MyTimeSetFragment
import kr.co.seoft.two_min.ui.preview.PreviewActivity
import kr.co.seoft.two_min.ui.proc.ProcActivity
import kr.co.seoft.two_min.ui.proc.ProcEndActivity
import kr.co.seoft.two_min.ui.proc.ProcExceedActivity
import kr.co.seoft.two_min.ui.save.SaveActivity
import kr.co.seoft.two_min.util.*


class MainActivity : ActivityHelper() {

    companion object {
        const val PROC_ACTIVITY = 1111
        const val PROC_END_ACTIVITY = 1112
        const val PROC_EXCEED_ACTIVITY = 1113
        const val SAVE_ACTIVITY = 1114
        const val PREVIEW_ACTIVITY = 1115
    }

    override val layoutResourceId = R.layout.activity_main

    var showStatusBottomButtons = false

    val db by lazy { AppDatabase.getDatabase(this) }

    val homeFragment by lazy {
        HomeFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initView()
        initListener()

    }

    fun startProc(timeSet: TimeSet) {
        ProcActivity.startProcActivity(this, timeSet, 5)
    }

    fun startSave(timeSet: TimeSet) {
        SaveActivity.startSaveActivity(this, timeSet)
    }

    private fun initView() {


        actHomeViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        toolbar.setTitle("")
                        homeFragment
                    }
                    else -> {
                        toolbar.setTitle("내 타임셋")
                        MyTimeSetFragment.newInstance()
                    }
                }
            }
        }

        actHomeViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    actHomeViewTabLayoutLine1.visibility = View.VISIBLE
                    actHomeViewTabLayoutLine2.visibility = View.INVISIBLE
                } else {
                    actHomeViewTabLayoutLine1.visibility = View.INVISIBLE
                    actHomeViewTabLayoutLine2.visibility = View.VISIBLE
                }
            }
        })

        actHomeTablayout.getTabAt(0)?.setIcon(R.drawable.dummy)
        actHomeTablayout.getTabAt(1)?.setIcon(R.drawable.dummy)

        TabLayoutMediator(actHomeTablayout, actHomeViewPager) { tab, position ->
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

        TedKeyboardObserver(this).listen(object : BaseKeyboardObserver.OnKeyboardListener {
            override fun onKeyboardChange(isShow: Boolean) {
                if (isShow) {
                    setShowTabLayout(false)
                    actHomeLlBottomButtons.visibility = View.INVISIBLE
                } else {
                    setShowTabLayout(true)
                    if (showStatusBottomButtons) actHomeLlBottomButtons.visibility = View.VISIBLE
                }
            }

        })

    }

    fun setLockViewpager(isLock: Boolean) {
        actHomeViewPager.isUserInputEnabled = !isLock
    }

    fun setTransparentToolbarAndBottoms(isTransparent: Boolean) {
        actMainViewTransparentTop.visibility = isTransparent.setVisible()
        actMainViewTransparentBottom.visibility = isTransparent.setVisible()
    }

    fun setShowBottomButtons(isShow: Boolean) {
        showStatusBottomButtons = isShow
        actHomeLlBottomButtons.visibility = isShow.setVisibleOrGone()
    }

    fun setShowTabLayout(isShow: Boolean) {
        actHomeLlTabLayoutLine.visibility = isShow.setVisibleOrGone()
        actHomeTablayout.visibility = isShow.setVisibleOrGone()
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
                    PreviewActivity.startSaveActivity(this, timeSetId)
                }
                PREVIEW_ACTIVITY -> {
                    val timeSetId = data.getLongExtra(PreviewActivity.RESP_TIME_SET_ID_FOR_START, 0L)

                    AppDatabase.getDatabase(this).timeSetDao().getTimeSetById(timeSetId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            startProc(it)
                        }, {
                            it.printStackTrace()
                        })


                }

            }
        }


    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(false) // true is back icon
            setHomeAsUpIndicator(R.drawable.btn_back)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.main_home_setting -> {
                "main_home_setting".toaste(this)
//                actHomeViewPager.isUserInputEnabled =true
            }
            R.id.main_home_history -> {
                "main_home_history".toaste(this)
//                actHomeViewPager.isUserInputEnabled =false
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

}
