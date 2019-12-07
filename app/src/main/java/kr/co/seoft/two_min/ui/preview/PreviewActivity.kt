package kr.co.seoft.two_min.ui.preview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_preview.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.*

class PreviewActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_preview

    companion object {

        private const val TAG = "PreviewActivity"

        const val EXTRA_TIME_SET_ID = "EXTRA_TIME_SET_ID"
        const val RESP_TIME_SET_ID_FOR_START = "RESP_TIME_SET_ID_FOR_START"

        fun startSaveActivity(context: Context, timeSetId: Long) {
            (context as Activity).startActivityForResult(
                Intent(context, PreviewActivity::class.java).apply {
                    putExtra(EXTRA_TIME_SET_ID, timeSetId)
                }, MainActivity.PREVIEW_ACTIVITY
            )
        }

    }

    lateinit var timeSet: TimeSet
    private val compositeDisposable = CompositeDisposable()
    var isLikeOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val timeSetId = intent.getLongExtra(EXTRA_TIME_SET_ID, 0L)

        compositeDisposable.add(

            AppDatabase.getDatabase(this).timeSetDao()
                .getTimeSetById(timeSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    timeSet = it
                    inits()
                }, {
                    it.printStackTrace()
                })

        )
    }

    fun inits() {
        initToolbar()
        initViews()
        initListener()
    }

    fun initViews() {

        actPreviewTvTitle.text = timeSet.title.toEditable()

        val sumOfTime = timeSet.times.sumBy { it.seconds }

        actPreviewTvTimeSetTime.text = sumOfTime.toFormattingString()

        actPreviewTvExpectEnd.text = sumOfTime.toEndTimeStrAfterSec()

        actPreviewTvSound.text = timeSet.times[0].bell.bellTypeToString()
        actPreviewTvComment.text = timeSet.times[0].comment

        isLikeOn = (timeSet.likeOrder != -1)
        invalidateOptionsMenu()

        actPreviewLsshlv.showLeftSideSnappyHorizontalListView(timeSet.times.asSequence().map { it.seconds }.toList())
    }

    fun initListener() {

        actPreviewBtBottom1Btn.setOnClickListener {
            finish()
        }

        actPreviewBtBottom2Btn.setOnClickListener {

            setResult(Activity.RESULT_OK, Intent().apply { putExtra(RESP_TIME_SET_ID_FOR_START, timeSet.timeSetId) })
            finish()
        }

        actPreviewLsshlv.setSelectedBadgeListener { item ->
            actPreviewTvSound.text = timeSet.times[item.index].bell.bellTypeToString()
            actPreviewTvComment.text = timeSet.times[item.index].comment
            actPreviewLsshlv.setFocus(item.index)
        }

    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isLikeOn) {
            menuInflater.inflate(R.menu.preview_on, menu)
        } else {
            menuInflater.inflate(R.menu.preview_off, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.preview_off_share -> {
                "preview_off_share".toaste(this)
            }
            R.id.preview_off_like -> {
                isLikeOn = !isLikeOn
                invalidateOptionsMenu()
                saveLikeToDatabase(true)
            }
            R.id.preview_on_share -> {
                "preview_on_share".toaste(this)
            }
            R.id.preview_on_like -> {
                isLikeOn = !isLikeOn
                invalidateOptionsMenu()
                saveLikeToDatabase(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveLikeToDatabase(isLike: Boolean) {
        compositeDisposable.add(
            Single.fromCallable {
                AppDatabase.getDatabase(this)
                    .timeSetDao()
                    .updateTimeSet(timeSet.apply {
                        likeOrder = if (isLike) 0 else -1
                    })
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    "like done".i(TAG)
                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
