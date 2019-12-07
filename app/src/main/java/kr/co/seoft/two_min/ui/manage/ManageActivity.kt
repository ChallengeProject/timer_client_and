package kr.co.seoft.two_min.ui.manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manage.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.util.dpToPx
import java.util.*

class ManageActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_manage

    companion object {

        private const val EXTRA_IS_LIKE_TIME_SET = "EXTRA_IS_LIKE_TIME_SET"

        fun startSaveActivity(context: Context, isLikeTimeSet: Boolean) {
            (context as Activity).startActivity(
                Intent(context, ManageActivity::class.java).apply {
                    putExtra(EXTRA_IS_LIKE_TIME_SET, isLikeTimeSet)
                }
            )
        }
    }

    private lateinit var curTimeSets: MutableList<TimeSet>
    private lateinit var curItemTouchHelper: ItemTouchHelper

    private var removeTimeSets = mutableListOf<TimeSet>()
    private lateinit var removeItemTouchHelper: ItemTouchHelper

    private var compositeDisposable = CompositeDisposable()

    val isLikeTimeSet by lazy {
        intent.getBooleanExtra(EXTRA_IS_LIKE_TIME_SET, false)
    }

    private val curAdapter by lazy {
        ManageAdapter(true) { type, vh ->
            when (type) {
                ManageAdapter.ManageCallbackType.ADD -> {
                } // empty
                ManageAdapter.ManageCallbackType.REMOVE -> {
                    removeTimeSet(vh)
                }
                ManageAdapter.ManageCallbackType.MOVE -> {
                    curItemTouchHelper.startDrag(vh)
                }
            }
        }
    }

    private val removeAdapter by lazy {
        ManageAdapter(false) { type, vh ->
            when (type) {
                ManageAdapter.ManageCallbackType.ADD -> {
                    addTimeSet(vh)
                }
                ManageAdapter.ManageCallbackType.REMOVE -> {
                } // empty
                ManageAdapter.ManageCallbackType.MOVE -> {
                    removeItemTouchHelper.startDrag(vh)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        initListener()
        initRecyclerViews()
        initTimeSets()
    }

    private fun initListener() {

        actManageTvCancel.setOnClickListener {
            finish()
        }

        actManageTvOk.setOnClickListener {
            Single.fromCallable {

                if (isLikeTimeSet) {
                    val dao = AppDatabase.getDatabase(this).timeSetDao()
                    curTimeSets.forEachIndexed { index, timeSet ->
                        dao.updateTimeSet(timeSet.apply { likeOrder = index })
                    }
                    removeTimeSets.forEach {
                        dao.updateTimeSet(it.apply { likeOrder = -1 })
                    }
                } else {
                    val dao = AppDatabase.getDatabase(this).timeSetDao()
                    curTimeSets.forEachIndexed { index, timeSet ->
                        dao.updateTimeSet(timeSet.apply { saveOrder = index })
                    }
                    removeTimeSets.forEach {
                        dao.deleteTimeSet(it)
                    }
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    finish()
                }, {
                    it.printStackTrace()
                })
        }

    }

    private fun initRecyclerViews() {
        actManageRvCurrent.adapter = curAdapter
        actManageRvCurrent.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 10.dpToPx()
            }
        })
        curItemTouchHelper = ItemTouchHelper(ManageItemTouchHelperCallback { from, to ->
            Collections.swap(curTimeSets, from.adapterPosition, to.adapterPosition)
            curAdapter.notifyItemMoved(from.adapterPosition, to.adapterPosition)
        })
        curItemTouchHelper.attachToRecyclerView(actManageRvCurrent)

        actManageRvRemove.adapter = removeAdapter
        actManageRvRemove.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 10.dpToPx()
            }
        })
        removeItemTouchHelper = ItemTouchHelper(ManageItemTouchHelperCallback { from, to ->
            Collections.swap(removeTimeSets, from.adapterPosition, to.adapterPosition)
            removeAdapter.notifyItemMoved(from.adapterPosition, to.adapterPosition)
        })
        removeItemTouchHelper.attachToRecyclerView(actManageRvRemove)
    }

    private fun initTimeSets() {

        if (isLikeTimeSet) {
            compositeDisposable.add(
                AppDatabase.getDatabase(this).timeSetDao().getTimeSetsOrderByLike()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        curTimeSets = it.toMutableList()
                        curAdapter.submitList(curTimeSets)
                    }, {
                        it.printStackTrace()
                    })
            )
        } else {
            compositeDisposable.add(
                AppDatabase.getDatabase(this).timeSetDao().getTimeSetsOrderBySave()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        curTimeSets = it.toMutableList()
                        curAdapter.submitList(curTimeSets)
                    }, {
                        it.printStackTrace()
                    })
            )
        }

        removeAdapter.submitList(removeTimeSets)
    }

    private fun addTimeSet(vh: ManageAdapter.ItemVH) {
        curTimeSets.add(removeTimeSets[vh.adapterPosition].copy())
        removeTimeSets.removeAt(vh.adapterPosition)
        curAdapter.notifyDataSetChanged()
        removeAdapter.notifyDataSetChanged()

        if (removeTimeSets.isEmpty())
            actManageTvRemoveText.visibility = View.GONE

    }

    private fun removeTimeSet(vh: ManageAdapter.ItemVH) {
        actManageTvRemoveText.visibility = View.VISIBLE
        removeTimeSets.add(curTimeSets[vh.adapterPosition].copy())
        curTimeSets.removeAt(vh.adapterPosition)
        curAdapter.notifyDataSetChanged()
        removeAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
