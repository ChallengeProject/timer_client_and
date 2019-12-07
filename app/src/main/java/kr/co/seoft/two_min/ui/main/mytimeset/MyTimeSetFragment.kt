package kr.co.seoft.two_min.ui.main.mytimeset

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
import kotlinx.android.synthetic.main.fragment_my_time_set.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.util.*


class MyTimeSetFragment : Fragment() {


    companion object {
        fun newInstance() = MyTimeSetFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_time_set, container, false)
    }

    val saveTimeSetAdapter by lazy {
        SaveTimeSetAdapter {
            it.e()
        }
    }

//    val adapter2 by lazy {
//        SaveTimeSetAdapter()
//    }

    var list2 = mutableListOf<String>()

    var compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragMyTimeSetRvSaveTimeSet.adapter = saveTimeSetAdapter
        fragMyTimeSetRvSaveTimeSet.addItemDecoration(object : RecyclerView.ItemDecoration() {
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


//        rv1.layoutManager = LinearLayoutManager(requireContext()).apply {
//            orientation = LinearLayoutManager.VERTICAL
//        }
//        rv2.layoutManager = LinearLayoutManager(requireContext())
//
//        rv1.adapter = adapter1
//        rv2.adapter = adapter2
//
//        btTest.setOnClickListener {
//
//            list1.add(Random.nextInt().toString())
//            list2.add(Random.nextInt().toString())
//            adapter1.submitList(list1.toMutableList())
//            adapter2.submitList(list2.toMutableList())
//        }

    }

    override fun onResume() {
        super.onResume()

        compositeDisposable.add(
            AppDatabase.getDatabase(requireContext()).timeSetDao().getTimeSetsOrderBySave()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    initSaveTimeSet(it)

                }, {
                    it.printStackTrace()
                })
        )

    }

    fun initSaveTimeSet(timeSets: List<TimeSet>) {
        if (timeSets.isEmpty()) {
            fragMyTimeSetClEmptyLayout.visibility = View.VISIBLE
            fragMyTimeSetTvSaveTimeSetManage.visibility = View.GONE
            fragMyTimeSetClSaveTimeSetFirstItem.visibility = View.GONE
            fragMyTimeSetRvSaveTimeSet.visibility = View.GONE
            fragMyTimeSetTvSaveTimeSetMore.visibility = View.GONE
//            fragMyTimeSetTvLikeTimeSetText.visibility = View.GONE
//            fragMyTimeSetTvLikeTimeSetManage.visibility = View.GONE
//            fragMyTimeSetRvLikeTimeSet.visibility = View.GONE
        } else {
            fragMyTimeSetClEmptyLayout.visibility = View.GONE
            fragMyTimeSetTvSaveTimeSetManage.visibility = View.VISIBLE
            fragMyTimeSetClSaveTimeSetFirstItem.visibility = View.VISIBLE
            fragMyTimeSetRvSaveTimeSet.visibility = View.VISIBLE

            if (timeSets.size >= 10) {
                fragMyTimeSetTvSaveTimeSetMore.visibility = View.VISIBLE
            } else {
                fragMyTimeSetTvSaveTimeSetMore.visibility = View.GONE
            }


//            fragMyTimeSetTvLikeTimeSetText.visibility = View.VISIBLE
//            fragMyTimeSetTvLikeTimeSetManage.visibility = View.VISIBLE
//            fragMyTimeSetRvLikeTimeSet.visibility = View.VISIBLE
//            fragMyTimeSetTvLikeTimeSetCheck.visibility = View.VISIBLE

            timeSets.first().run {
                fragMyTimeSetTvSaveTimeSetFirstItemTimeCount.text = this.times.size.toString()
                fragMyTimeSetTvSaveTimeSetFirstItemEndTime.text = "종료 예정 ${this.wholeTime.toEndTimeStrAfterSec()}"
                fragMyTimeSetTvSaveTimeSetFirstItemTitle.text = this.title
                fragMyTimeSetTvSaveTimeSetFirstItemWholeTime.text = this.wholeTime.x1000L().toTimeStr()
            }

            saveTimeSetAdapter.submitList(timeSets.drop(1).take(8))
        }

    }

    fun initLikeTimeSet() {

    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

