package kr.co.seoft.two_min.ui.history

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_histories.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.main.MainActivity.Companion.HISTORIES_ACTIVITY
import kr.co.seoft.two_min.ui.preview.PreviewActivity
import kr.co.seoft.two_min.util.dpToPx
import kr.co.seoft.two_min.util.setupActionBar

class HistoriesActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_histories

    companion object {

        private const val TAG = "HistoriesActivity"
        const val HISTORY_ACTIVITY = 1000

        const val EXTRA_TIME_SET_ID = "EXTRA_TIME_SET_ID"

        fun startHistoriesActivity(context: Context, timeSetId: Long = -1) {
            (context as Activity).startActivityForResult(
                Intent(context, HistoriesActivity::class.java).apply {
                    putExtra(EXTRA_TIME_SET_ID, timeSetId)
                }, HISTORIES_ACTIVITY
            )
        }
    }

    val db by lazy { AppDatabase.getDatabase(this) }

    lateinit var historiesAdapter: HistoriesAdapter

    private val compositeDisposable = CompositeDisposable()

    val timeSetId by lazy {
        intent.getLongExtra(PreviewActivity.EXTRA_TIME_SET_ID, -1L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바로실행
        if (timeSetId != -1L) {

        }

        initToolbar()
        initViews()
        initListener()
    }

    fun initViews() {

        historiesAdapter = HistoriesAdapter {
            HistoryActivity.startHistoryActivity(this, it.historyId)
        }

        actHistoriesRv.adapter = historiesAdapter
        actHistoriesRv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 10.dpToPx()
            }
        })

        compositeDisposable.add(
            db.timeSetDao().getHistories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isEmpty()) actHistoriesLlEmptyLayout.visibility = View.VISIBLE
                    historiesAdapter.submitList(it)
                }, {
                    it.printStackTrace()
                })
        )

        val emptyText = actHistoriesTvEmptyText.text.toString()
        val ss = SpannableString(emptyText)
        ss.setSpan(UnderlineSpan(), 0, emptyText.length, 0)
        actHistoriesTvEmptyText.text = ss
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val nonNullIntent = data ?: return

        if (requestCode == HISTORY_ACTIVITY && resultCode == Activity.RESULT_OK) {

            setResult(Activity.RESULT_OK,
                Intent().apply {
                    putExtra(
                        HistoryActivity.RESP_TIME_SET,
                        nonNullIntent.getParcelableExtra(HistoryActivity.RESP_TIME_SET) as TimeSet
                    )
                    putExtra(
                        HistoryActivity.RESP_HISTORY_RESPONSE_TYPE,
                        nonNullIntent.getIntExtra(HistoryActivity.RESP_HISTORY_RESPONSE_TYPE, 0)
                    )
                })
            finish()
        }

    }

    fun initListener() {
        actHistoriesTvEmptyText.setOnClickListener {
            finish()
        }

    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.___ic_back)
            setTitle("히스토리")
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
