package com.timer.proc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timer.R
import kotlinx.android.synthetic.main.activity_proc.*

class ProcActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc)

        var k = 51


        rvBadges.addHomeBadge( ProcBadge( count = 0, second = k++, type = ProcBadgeType.NORMAL ) )
        rvBadges.addHomeBadge( ProcBadge( count = 0, second = k++, type = ProcBadgeType.NORMAL ) )
        rvBadges.addHomeBadge( ProcBadge( count = 0, second = k++, type = ProcBadgeType.NORMAL ) )
        rvBadges.addHomeBadge( ProcBadge( count = 0, second = k++, type = ProcBadgeType.NORMAL ) )
        rvBadges.addHomeBadge( ProcBadge( count = 0, second = k++, type = ProcBadgeType.NORMAL ) )
        rvBadges.addHomeBadge( ProcBadge( count = 0, second = k++, type = ProcBadgeType.NORMAL ) )
        rvBadges.addHomeBadge( ProcBadge( count = 0, second = k++, type = ProcBadgeType.NORMAL ) )


        tvTimesetTimeText.setOnClickListener { rvBadges.setFocus(1) }
        tvEndTimeText.setOnClickListener { rvBadges.setFocus(2) }
        tvSoundText.setOnClickListener { rvBadges.setFocus(3) }
        tvCommentText.setOnClickListener { rvBadges.setFocus(4) }















    }






}
