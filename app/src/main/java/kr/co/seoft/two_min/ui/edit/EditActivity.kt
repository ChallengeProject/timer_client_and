package kr.co.seoft.two_min.ui.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.ActivityHelperForFrag
import kr.co.seoft.two_min.ui.main.home.HomeFragment
import kr.co.seoft.two_min.util.e
import kr.co.seoft.two_min.util.setVisible
import kr.co.seoft.two_min.util.setupActionBar
import kr.co.seoft.two_min.util.toaste

class EditActivity : ActivityHelperForFrag() {

    override val layoutResourceId = R.layout.activity_edit

    val homeFragment by lazy {
        HomeFragment.newInstance()
    }

    companion object {

        private const val EXTRA_TIME_SET_ID = "EXTRA_TIME_SET_ID"

        fun startEditActivity(context: Context, timeSetId: Long) {
            (context as Activity).startActivity(
                Intent(context, EditActivity::class.java).apply {
                    putExtra(EXTRA_TIME_SET_ID, timeSetId)
                }
            )
        }
    }

    var timeSetId = 0L
    private var compositeDisposable = CompositeDisposable()
    lateinit var curTimeSet: TimeSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeSetId = intent.getLongExtra(EXTRA_TIME_SET_ID, 0)

        initToolbar()
        initView()
        initListener()

        compositeDisposable.add(
            db.timeSetDao()
                .getTimeSetById(timeSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    curTimeSet = it
                    homeFragment.loadTimeSet(it)
                }, {
                    it.printStackTrace()
                })
        )


    }


    private fun initView() {
        supportFragmentManager.beginTransaction().replace(R.id.actEditFrag, homeFragment).commit()
    }

    fun initListener() {
        actEditBtBottom1Btn.setOnClickListener {
            finish()
        }

        actEditBtBottom2Btn.setOnClickListener {
            homeFragment.requestSave()
        }
        actEditViewTransparentTop.setOnClickListener { /*pass*/ }
        actEditViewTransparentBottom.setOnClickListener { /*pass*/ }

        TedKeyboardObserver(this).listen(object : BaseKeyboardObserver.OnKeyboardListener {
            override fun onKeyboardChange(isShow: Boolean) {
                if (isShow) {
                    actEditLlBottomButtons.visibility = View.INVISIBLE
                } else {
                    actEditLlBottomButtons.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun startProc(timeSet: TimeSet) {
        // pass
    }

    override fun startSave(timeSet: TimeSet) {
        compositeDisposable.add(Single.fromCallable {
            db.timeSetDao().updateTimeSet(
                curTimeSet.copy().apply { times = timeSet.times }
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                finish()
            }, {
                it.printStackTrace()
            })
        )
    }

    override fun setShowBottomButtons(isShow: Boolean) {
        // pass
    }

    override fun setLockViewpager(isLock: Boolean) {
        // pass
    }

    override fun setTransparentToolbarAndBottoms(isTransparent: Boolean) {
        actEditViewTransparentTop.visibility = isTransparent.setVisible()
        actEditViewTransparentBottom.visibility = isTransparent.setVisible()
    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.btn_back)
            setTitle("타임셋 편집")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.edit_remove -> {
                compositeDisposable.add(Single.fromCallable {
                    db.timeSetDao().deleteTimeSet(curTimeSet)
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        finish()
                    },{
                        it.printStackTrace()
                    }))
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
