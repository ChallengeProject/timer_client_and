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
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.ui.manage.ManageActivity
import kr.co.seoft.two_min.util.dpToPx
import kr.co.seoft.two_min.util.toEndTimeStrAfterSec
import kr.co.seoft.two_min.util.toTimeStr
import kr.co.seoft.two_min.util.x1000L


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

    private val likeTimeSetAdapter by lazy {
        LikeTimeSetAdapter {
            act.startPreviewActivity(it.timeSetId)
        }
    }

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

        fragMyTimeSetRvLikeTimeSet.adapter = likeTimeSetAdapter
        fragMyTimeSetRvLikeTimeSet.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 10.dpToPx()
            }
        })

        initListener()
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

        compositeDisposable.add(
            AppDatabase.getDatabase(requireContext()).timeSetDao().getTimeSetsOrderByLike()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    initLikeTimeSet(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun initListener(){

        fragMyTimeSetTvSaveTimeSetManage.setOnClickListener {
            ManageActivity.startSaveActivity(requireContext(),false)
        }

        fragMyTimeSetTvLikeTimeSetManage.setOnClickListener {
            ManageActivity.startSaveActivity(requireContext(),true)
        }


    }

    private fun initSaveTimeSet(timeSets: List<TimeSet>) {
        if (timeSets.isEmpty()) {
            fragMyTimeSetClEmptyLayout.visibility = View.VISIBLE
            fragMyTimeSetTvSaveTimeSetManage.visibility = View.GONE
            fragMyTimeSetClSaveTimeSetFirstItem.visibility = View.GONE
            fragMyTimeSetRvSaveTimeSet.visibility = View.GONE
            fragMyTimeSetTvSaveTimeSetMore.visibility = View.GONE
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

            timeSets.first().run {
                fragMyTimeSetTvSaveTimeSetFirstItemTimeCount.text = this.times.size.toString()
                fragMyTimeSetTvSaveTimeSetFirstItemEndTime.text = "종료 예정 ${this.wholeTime.toEndTimeStrAfterSec()}"
                fragMyTimeSetTvSaveTimeSetFirstItemTitle.text = this.title
                fragMyTimeSetTvSaveTimeSetFirstItemWholeTime.text = this.wholeTime.x1000L().toTimeStr()
            }

            fragMyTimeSetClSaveTimeSetFirstItem.setOnClickListener {
                act.startPreviewActivity(timeSets[0].timeSetId)
            }

            saveTimeSetAdapter.submitList(timeSets.drop(1).take(8))
        }

    }

    private fun initLikeTimeSet(likeTimeSets: List<TimeSet>) {

        if (likeTimeSets.isEmpty()) {
            fragMyTimeSetTvLikeTimeSetText.visibility = View.GONE
            fragMyTimeSetTvLikeTimeSetManage.visibility = View.GONE
            fragMyTimeSetRvLikeTimeSet.visibility = View.GONE
            return
        } else {
            fragMyTimeSetTvLikeTimeSetText.visibility = View.VISIBLE
            fragMyTimeSetTvLikeTimeSetManage.visibility = View.VISIBLE
            fragMyTimeSetRvLikeTimeSet.visibility = View.VISIBLE
        }

        if (likeTimeSets.size >= 11) {
            fragMyTimeSetTvLikeTimeSetMore.visibility = View.VISIBLE
        } else {
            fragMyTimeSetTvLikeTimeSetMore.visibility = View.GONE
        }

        likeTimeSetAdapter.submitList(likeTimeSets.take(10))
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

