package com.timer.timeset.proc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timer.R
import com.timer.util.TimesetBadge
import com.timer.util.TimesetBadgeType
import com.timer.util.i
import kotlinx.android.synthetic.main.fragment_proc_timeset.*

class ProcTimeSetFragment : Fragment() {

    val TAG = "ProcTimeSetFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_proc_timeset, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvHTRV.addTimesetBadge(TimesetBadge(second = 2, type = TimesetBadgeType.NORMAL))
        rvHTRV.addTimesetBadge(TimesetBadge(second = 82, type = TimesetBadgeType.NORMAL))
        rvHTRV.addTimesetBadge(TimesetBadge(second = 182, type = TimesetBadgeType.NORMAL))

        tvTopExplain.text = "기본음"

        tvTime.text = "01:20:00"
        tvTimeSetName.text = "타임셋명"

        tvEndTime.text = "10:57 AM"
        tvWholeTime.text = "00시간 00분 00초"

        tvBell.text = "기본음"
        tvComment.text = "코맨트"

        btCancel.setOnClickListener { _ ->
            rvHTRV.showTopIcon()
        }

        rvHTRV.onBadgeSelectedListener =  { pos , topIconXCoordinate->
            "$pos $topIconXCoordinate".i(TAG)

        }

    }
}