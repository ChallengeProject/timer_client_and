package kr.co.seoft.two_min.ui.main.preset

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_preset.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.ApiService
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.data.Time
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.ui.main.mytimeset.SaveTimeSetAdapter
import kr.co.seoft.two_min.ui.proc.ProcActivity
import kr.co.seoft.two_min.util.*

class PresetFragment : Fragment() {

    companion object {
        fun newInstance() = PresetFragment()
    }

    val act by lazy {
        activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preset, container, false)
    }

    private val timeSetAdapter by lazy {
        SaveTimeSetAdapter {
            ProcActivity.startProcActivity(act, it, Preferencer.getCountDown(act), false)
        }
    }

    var compositeDisposable = CompositeDisposable()

    private val service = ApiService.create()

    private val clHot by lazy { listOf(fragPresetClHot1, fragPresetClHot2, fragPresetClHot3) }
    private val tvHotWholeTime by lazy { listOf(fragPresetTvHot1WholeTime, fragPresetTvHot2WholeTime, fragPresetTvHot3WholeTime) }
    private val tvHotEndTime by lazy { listOf(fragPresetTvHot1EndTime, fragPresetTvHot2EndTime, fragPresetTvHot3EndTime) }
    private val tvHotTitle by lazy { listOf(fragPresetTvHot1Title, fragPresetTvHot2Title, fragPresetTvHot3Title) }
    private val tvHotTimeCount by lazy { listOf(fragPresetTvHot1TimeCount, fragPresetTvHot2TimeCount, fragPresetTvHot3TimeCount) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragPresetRvPresets.adapter = timeSetAdapter
        fragPresetRvPresets.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val pos = parent.getChildAdapterPosition(view)
                if (pos % 2 == 0) {
                    outRect.right = 5.dpToPx()
                } else {
                    outRect.left = 5.dpToPx()
                }
                outRect.bottom = 10.dpToPx()
            }
        })

        initListener()
    }

    override fun onResume() {
        super.onResume()

        loadPresets()
    }

    private fun loadPresets() {


        compositeDisposable.add(

            service.getHotPreset()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEachIndexed { index, apiTimeSet ->
                        val timeSet = TimeSet(
                            apiTimeSet.title,
                            apiTimeSet.timers.map {
                                Time(
                                    it.end,
                                    it.comment,
                                    Bell(
                                        when (it.alarm) {
                                            1 -> Bell.Type.VIBRATION
                                            2 -> Bell.Type.SLIENT
                                            else -> Bell.Type.DEFAULT
                                        }
                                    )
                                )
                            })

                        if (index == 0) fragPresetTvPickText.visibility = View.VISIBLE

                        clHot[index].visibility = View.VISIBLE
                        tvHotWholeTime[index].text = timeSet.wholeTime.x1000L().toTimeStr()
                        tvHotEndTime[index].text = "종료 예정 ${timeSet.wholeTime.toEndTimeStrAfterSec()}"
                        tvHotTitle[index].text = timeSet.title
                        tvHotTimeCount[index].text = timeSet.times.size.toString()

                        clHot[index].setOnClickListener {
                            ProcActivity.startProcActivity(act, timeSet, Preferencer.getCountDown(act), apiTimeSet.isRepeat)
                        }

                        fragPresetPb.visibility = View.GONE
                    }
                }, {
                    it.printStackTrace()
                })
        )

        compositeDisposable.add(

            service.getBasicPreset()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val list = it.map {
                        TimeSet(
                            it.title,
                            it.timers.map {
                                Time(
                                    it.end,
                                    it.comment,
                                    Bell(
                                        when (it.alarm) {
                                            1 -> Bell.Type.VIBRATION
                                            2 -> Bell.Type.SLIENT
                                            else -> Bell.Type.DEFAULT
                                        }
                                    )
                                )
                            })
                    }

                    if (list.isNotEmpty()) fragPresetTvBasicText.visibility = View.VISIBLE
                    timeSetAdapter.submitList(list)

                    fragPresetPb.visibility = View.GONE
                }, {
                    it.printStackTrace()
                })
        )


    }

    fun initListener() {


    }

    private fun initSaveTimeSet(timeSets: List<TimeSet>) {
//        if (timeSets.isEmpty()) {
//            fragPresetClEmptyLayout.visibility = View.VISIBLE
//            fragPresetTvSaveTimeSetManage.visibility = View.GONE
//            fragPresetClSaveTimeSetFirstItem.visibility = View.GONE
//            fragPresetRvSaveTimeSet.visibility = View.GONE
//            fragPresetTvSaveTimeSetMore.visibility = View.GONE
//        } else {
//            fragPresetClEmptyLayout.visibility = View.GONE
//            fragPresetTvSaveTimeSetManage.visibility = View.VISIBLE
//            fragPresetClSaveTimeSetFirstItem.visibility = View.VISIBLE
//            fragPresetRvSaveTimeSet.visibility = View.VISIBLE
//
//            if (timeSets.size >= 10) {
//                fragPresetTvSaveTimeSetMore.visibility = View.VISIBLE
//            } else {
//                fragPresetTvSaveTimeSetMore.visibility = View.GONE
//            }
//
//            timeSets.first().run {
//                fragPresetTvSaveTimeSetFirstItemTimeCount.text = this.times.size.toString()
//                fragPresetTvSaveTimeSetFirstItemEndTime.text = "종료 예정 ${this.wholeTime.toEndTimeStrAfterSec()}"
//                fragPresetTvSaveTimeSetFirstItemTitle.text = this.title
//                fragPresetTvSaveTimeSetFirstItemWholeTime.text = this.wholeTime.x1000L().toTimeStr()
//            }
//
//            fragPresetClSaveTimeSetFirstItem.setOnClickListener {
//                act.startPreviewActivity(timeSets[0].timeSetId)
//            }
//
//            saveTimeSetAdapter.submitList(timeSets.drop(1).take(8))
//        }

    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

