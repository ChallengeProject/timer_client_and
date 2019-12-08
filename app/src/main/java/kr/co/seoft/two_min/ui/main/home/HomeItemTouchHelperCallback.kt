package kr.co.seoft.two_min.ui.main.home


import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


// ref : http://dudmy.net/android/2018/05/02/drag-and-swipe-recyclerview , https://github.com/dudmy/blog-sample
class HomeItemTouchHelperCallback(
    private val moveCb: (RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Unit,
    private val endCb: (Int) -> Unit
) : ItemTouchHelper.Callback() {

    // isMoved 처리 안하면 crash, but ManageActivityy에서는 잘됨?? 여유있을때 확인
    private var isMoved = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        moveCb.invoke(viewHolder, target)
        isMoved = true
        return true
    }

    /**
     * call for refresh badge's bottom numbers
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (isMoved) {
            endCb.invoke(0)
            isMoved = false
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun isLongPressDragEnabled() = false

}