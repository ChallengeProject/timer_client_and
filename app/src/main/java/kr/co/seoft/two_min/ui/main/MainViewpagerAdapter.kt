//package kr.co.seoft.two_min.ui.main
//
//import androidx.viewpager2.widget.ViewPager2
//import android.content.Context
//import android.widget.RelativeLayout
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import android.view.ViewGroup
//import android.view.LayoutInflater
//import android.view.View
//import androidx.core.content.ContextCompat
//import kr.co.seoft.two_min.R
//
//
//class MainViewpagerAdapter : RecyclerView.Adapter<MainViewpagerAdapter.EventViewHolder>() {
//    val eventList = listOf("0", "1", "2")
//
//    // Layout "layout_demo_viewpager2_cell.xml" will be defined later
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
//        EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.size1, parent, false))
//
//    override fun getItemCount() = eventList.count()
//    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
//        (holder.view as? TextView)?.also {
//            it.text = "Page " + eventList[position]
//
//            val backgroundColorResId =
//                if (position % 2 == 0) R.color.colorAccent else R.color.colorPrimary
//            it.setBackgroundColor(ContextCompat.getColor(it.context, backgroundColorResId))
//        }
//    }
//
//    class EventViewHolder(val view: View) : RecyclerView.ViewHolder(view)
//}