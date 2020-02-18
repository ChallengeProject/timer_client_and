package kr.co.seoft.two_min.ui.main.mytimeset

import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my_time_set.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.ui.manage.ManageActivity
import kr.co.seoft.two_min.ui.proc.ProcActivity
import kr.co.seoft.two_min.util.*


class MyTimeSetFragment : Fragment() {


    companion object {
        fun newInstance() = MyTimeSetFragment()
    }

    val act by lazy {
        activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_time_set, container, false)
    }

    private val saveTimeSetAdapter by lazy {
        SaveTimeSetAdapter {
            act.startPreviewActivity(it.timeSetId)
        }
    }
    private val recentlyAdapter by lazy {
        SaveTimeSetAdapter {
            ProcActivity.startProcActivity(act, it, Preferencer.getCountDown(act), false)
        }
    }

    var compositeDisposable = CompositeDisposable()

    private val clHot by lazy { listOf(fragMyTimeSetClHot1, fragMyTimeSetClHot2, fragMyTimeSetClHot3) }
    private val tvHotWholeTime by lazy { listOf(fragMyTimeSetTvHot1WholeTime, fragMyTimeSetTvHot2WholeTime, fragMyTimeSetTvHot3WholeTime) }
    private val tvHotEndTime by lazy { listOf(fragMyTimeSetTvHot1EndTime, fragMyTimeSetTvHot2EndTime, fragMyTimeSetTvHot3EndTime) }
    private val tvHotTitle by lazy { listOf(fragMyTimeSetTvHot1Title, fragMyTimeSetTvHot2Title, fragMyTimeSetTvHot3Title) }
    private val tvHotTimeCount by lazy { listOf(fragMyTimeSetTvHot1TimeCount, fragMyTimeSetTvHot2TimeCount, fragMyTimeSetTvHot3TimeCount) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragMyTimeSetRvSaveTimeSet.adapter = saveTimeSetAdapter
        fragMyTimeSetRvSaveTimeSet.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 10.dpToPx()
            }
        })

        fragMyTimeSetRvRecentlyTimeSet.adapter = recentlyAdapter
        fragMyTimeSetRvRecentlyTimeSet.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 10.dpToPx()
            }
        })

        val emptyText = fragMyTimeSetTvSaveTimeSetManage.text.toString()
        val ss = SpannableString(emptyText)
        ss.setSpan(UnderlineSpan(), 0, emptyText.length, 0)
        fragMyTimeSetTvSaveTimeSetManage.text = ss

        initListener()
    }

    override fun onResume() {
        super.onResume()

        loadSaveTimeSet()
    }

    fun loadSaveTimeSet() {
        compositeDisposable.add(
            AppDatabase.getDatabase(requireContext()).timeSetDao().getTimeSetsOrderBySave()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    initSaveTimeSet(it)

                    fragMyTimeSetPb.visibility = View.GONE
                }, {
                    it.printStackTrace()
                })
        )

        val recentlies = Preferencer.getRecentlyTimeSet(act).filterNot {
            it.title == Preferencer.EMPTY_TIME_SET_TITLE
        }.toList()

        if (recentlies.isNotEmpty()) {
            fragMyTimeSetRvRecentlyTimeSet.visibility = View.VISIBLE
            fragMyTimeSetTvRecentlyTimeSetText.visibility = View.VISIBLE

            recentlyAdapter.submitList(recentlies)
        }
    }

    fun initListener() {

        fragMyTimeSetTvSaveTimeSetManage.setOnClickListener {
            ManageActivity.startSaveActivity(requireContext())
        }

        fragMyTimeSetClEmptyLayout.setOnClickListener {
            (requireActivity() as MainActivity).movePage(0)
        }
    }

    private fun initSaveTimeSet(timeSets: List<TimeSet>) {
        if (timeSets.isEmpty()) {
            fragMyTimeSetClEmptyLayout.visibility = View.VISIBLE
            fragMyTimeSetTvSaveTimeSetManage.visibility = View.GONE
            fragMyTimeSetRvSaveTimeSet.visibility = View.GONE
            fragMyTimeSetTvSaveTimeSetMore.visibility = View.GONE
        } else {
            fragMyTimeSetClEmptyLayout.visibility = View.GONE
            fragMyTimeSetTvSaveTimeSetManage.visibility = View.VISIBLE
            fragMyTimeSetRvSaveTimeSet.visibility = View.VISIBLE

            if (timeSets.size >= 10) {
                fragMyTimeSetTvSaveTimeSetMore.visibility = View.VISIBLE
            } else {
                fragMyTimeSetTvSaveTimeSetMore.visibility = View.GONE
            }
        }

        timeSets.forEachIndexed { index, timeSet ->
            if (index >= 3) return@forEachIndexed
            clHot[index].visibility = View.VISIBLE
            tvHotWholeTime[index].text = timeSet.wholeTime.x1000L().toTimeStr()
            tvHotEndTime[index].text = "${getString(R.string.scheduled_to_end)} ${timeSet.wholeTime.toEndTimeStrAfterSec()}"
            tvHotTitle[index].text = timeSet.title
            tvHotTimeCount[index].text = timeSet.times.size.toString()

            clHot[index].setOnClickListener {
                act.startPreviewActivity(timeSets[index].timeSetId)
            }
        }

        saveTimeSetAdapter.submitList(timeSets.drop(3).take(7))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

