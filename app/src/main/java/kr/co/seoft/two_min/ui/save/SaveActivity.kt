package kr.co.seoft.two_min.ui.save

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import gun0912.tedkeyboardobserver.BaseKeyboardObserver
import gun0912.tedkeyboardobserver.TedKeyboardObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_save.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.*

class SaveActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_save

    companion object {

        const val EXTRA_TIME_SET = "EXTRA_TIME_SET"
        const val RESP_TIME_SET_ID = "RESP_TIME_SET_ID"

        fun startSaveActivity(context: Context, timeSet: TimeSet) {
            (context as Activity).startActivityForResult(
                Intent(context, SaveActivity::class.java).apply {
                    putExtra(EXTRA_TIME_SET, timeSet)
                }, MainActivity.SAVE_ACTIVITY
            )
        }

    }

    lateinit var timeSet: TimeSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeSet = intent.getParcelableExtra(EXTRA_TIME_SET) as TimeSet

        initToolbar()
        initViews()
        initListener()

        timeSet.e()


    }

    fun initViews() {

        actSaveEtTitle.text = timeSet.title.toEditable()

        val sumOfTime = timeSet.times.sumBy { it.seconds }

        actSaveTvTimeSetTime.text = sumOfTime.toFormattingString()

        actSaveTvExpectEnd.text = sumOfTime.toEndTimeStrAfterSec()

        actSaveTvSound.text = timeSet.times[0].bell.bellTypeToString()
        actSaveTvComment.text = timeSet.times[0].comment

        actSaveLsshlv.showLeftSideSnappyHorizontalListView(timeSet.times.asSequence().map { it.seconds }.toList())
    }

    fun initListener() {
        actSaveIvClose.setOnClickListener {
            actSaveEtTitle.text = "".toEditable()
        }

        actSaveBtBottom1Btn.setOnClickListener {
            finish()
        }

        actSaveBtBottom2Btn.setOnClickListener {
            AppDatabase.getDatabase(this).timeSetDao()
                .insertTimeSet(timeSet.apply {
                    title = actSaveEtTitle.text.toString()
                    useHistory = 0
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setResult(
                        Activity.RESULT_OK,
                        Intent().apply { putExtra(RESP_TIME_SET_ID, it) }
                    )
                    finish()
                }, {
                    it.printStackTrace()
                })
        }

        actSaveLsshlv.setSelectedBadgeListener { item ->
            actSaveTvSound.text = timeSet.times[item.index].bell.bellTypeToString()
            actSaveTvComment.text = timeSet.times[item.index].comment
            actSaveLsshlv.setFocus(item.index)
        }


        TedKeyboardObserver(this).listen(object : BaseKeyboardObserver.OnKeyboardListener {
            override fun onKeyboardChange(isShow: Boolean) {
                actSaveLlBottomButtons.visibility = (!isShow).setVisible()
            }
        })
    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = "타임셋 저장"
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


}
