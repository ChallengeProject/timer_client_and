//package com.timer.timeset.proc
//
//import android.app.Activity
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.timer.R
//import com.timer.util.TimesetBadge
//import com.timer.util.TimesetBadgeType
//import com.timer.util.i
//import kotlinx.android.synthetic.main.fragment_proc_timeset.*
//
//
//class ProcTimeSetFragment : Fragment() {
//
//    val TAG = "ProcTimeSetFragment"
//
//    lateinit var times: ArrayList<Int>
//    lateinit var act:Activity
//    lateinit var updater: ProcViewUpdater
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_proc_timeset, container, false)
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        act = this.activity!!
//        updater = ProcViewUpdater(act)
//
//        initProperties()
//        initViews()
//    }
//
//    fun initProperties() {
//        times = arrayListOf(30, 40, 50, 150)
//    }
//
//    fun initViews() {
//
//        times.forEach {
//            rvHTRV.addTimesetBadge(TimesetBadge(second = it, type = TimesetBadgeType.NORMAL))
//        }
//
//        tvTopNotification.text = "시작 5초 전"
//
//        tvTime.text = "01:20:00"
//        tvTimeSetName.text = "타임셋명"
//
//        tvEndTime.text = "10:57 AM"
//        tvWholeTime.text = "00시간 00분 00초"
//
//        tvAlarm.text = "기본음"
//        tvComment.text = "코맨트"
//
//        btLeft.setOnClickListener { _ ->
//            updater.showSkipMessage(rvHTRV.latelyMidPosX)
//        }
//
//        btRight.setOnClickListener {
//
//
//        }
//
//        rvHTRV.onBadgeSelectedListener = { pos ->
//            "onBadgeSelectedListener callback $pos".i(TAG)
//            updater.hideSkipMessage()
//        }
//    }
//
//}