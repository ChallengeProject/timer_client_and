package kr.co.seoft.two_min.ui.manage

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// ref : http://dudmy.net/android/2018/05/02/drag-and-swipe-recyclerview , https://github.com/dudmy/blog-sample
class ManageItemTouchHelperCallback(
    private val moveCb: (RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Unit
) : ItemTouchHelper.Callback() {


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN

        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (viewHolder.itemView == target.itemView) return false
        moveCb.invoke(viewHolder, target)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun isLongPressDragEnabled() = false

}