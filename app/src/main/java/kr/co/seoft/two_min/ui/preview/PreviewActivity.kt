package kr.co.seoft.two_min.ui.preview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_preview.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.edit.EditActivity
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.setupActionBar
import kr.co.seoft.two_min.util.toEndTimeStrAfterSec
import kr.co.seoft.two_min.util.toFormattingString

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

    val timeSetId by lazy {
        intent.getLongExtra(EXTRA_TIME_SET_ID, 0L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

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
                    // EditAct 에서 타임셋이 삭제될 경우 여기로 빠지게되고 자동으로 finish, 밖에서 rearrange 처리됨
                    finish()
                })
        )
    }

    private fun inits() {
        initToolbar()
        initViews()
        initListener()
    }

    private fun initViews() {

        actPreviewTvTitle.text = timeSet.title

        val sumOfTime = timeSet.times.sumBy { it.seconds }

        actPreviewTvTimeSetTime.text = sumOfTime.toFormattingString()

        actPreviewTvExpectEnd.text = sumOfTime.toEndTimeStrAfterSec()

        actPreviewTvSound.text = timeSet.times[0].bell.bellTypeToString()
        actPreviewTvComment.text = timeSet.times[0].comment

        invalidateOptionsMenu()

        actPreviewLsshlv.showLeftSideSnappyHorizontalListView(timeSet.times.asSequence().map { it.seconds }.toList())
    }

    private fun initListener() {

        actPreviewBtBottom1Btn.setOnClickListener {

            EditActivity.startEditActivity(this, timeSet.timeSetId)
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
            setHomeAsUpIndicator(R.drawable._ic_back)
            setTitle("")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
