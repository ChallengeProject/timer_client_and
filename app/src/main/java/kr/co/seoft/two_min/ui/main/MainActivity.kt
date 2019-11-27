package kr.co.seoft.two_min.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.ui.proc.ProcActivity
import kr.co.seoft.two_min.ui.proc.ProcEndActivity
import kr.co.seoft.two_min.ui.proc.ProcExceedActivity
import kr.co.seoft.two_min.util.i
import kr.co.seoft.two_min.util.setupActionBar
import kr.co.seoft.two_min.util.toaste

class MainActivity : ActivityHelper() {

    companion object {
        const val PROC_ACTIVITY = 1111
        const val PROC_END_ACTIVITY = 1112
        const val PROC_EXCEED_ACTIVITY = 1113
    }

    override val layoutResourceId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initView()



//        ProcActivity.startProcActivity(this,TimeSet(
//            "AA",5,
//            listOf(Time(3),Time(3), Time(3))
//        ))


    }

    private fun initView() {
        actHomeViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        SizeFragment1.newInstance()
                    }
                    else -> {
                        SizeFragment2.newInstance()
                    }
                }
            }
        }

        actHomeViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)


                if(position == 0) {
                    actHomeViewTabLayoutLine1.visibility = View.VISIBLE
                    actHomeViewTabLayoutLine2.visibility = View.INVISIBLE
                } else {
                    actHomeViewTabLayoutLine1.visibility = View.INVISIBLE
                    actHomeViewTabLayoutLine2.visibility = View.VISIBLE
                }

            }

        })

        actHomeTablayout.getTabAt(0)?.setIcon(R.drawable.dummy)
        actHomeTablayout.getTabAt(1)?.setIcon(R.drawable.dummy)

        TabLayoutMediator(actHomeTablayout, actHomeViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text  = "Size"
                    tab.setIcon(R.drawable.dummy)
                }
                else -> {
                    tab.text  = "Template"
                    tab.setIcon(R.drawable.btn_back)
                }
            }
        }.attach()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        "onActivityResult111".i()

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PROC_ACTIVITY -> {
                    "PROC_ACTIVITY in Main".i()
                    ProcEndActivity.startProcEndActivity(
                        this,
                        data.getParcelableExtra(ProcActivity.RESP_TIME_SET),
                        data.getParcelableExtra(ProcActivity.RESP_USE_INFO)
                    )
                }
                PROC_END_ACTIVITY -> {
                    "PROC_END_ACTIVITY in Main".i()
                    val timeSet = data.getParcelableExtra<TimeSet>(ProcEndActivity.RESP_TIME_SET)
                    when (data.getStringExtra(ProcEndActivity.RESP_TYPE)) {

                        ProcEndActivity.RESP_TYPE_EXCEED -> {
                            ProcExceedActivity.startProcExceedActivity(this, timeSet)
                        }
                        ProcEndActivity.RESP_TYPE_RESTART -> {
                            ProcActivity.startProcActivity(this, timeSet)
                        }
                        ProcEndActivity.RESP_TYPE_SAVE -> {
                            // TODO 저장 구체화되면 ㄱㄱ
                        }
                    }
                }
                PROC_EXCEED_ACTIVITY -> {
                    "PROC_EXCEED_ACTIVITY in Main".i()
                }

            }
        }


    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(false) // true is back icon
            setHomeAsUpIndicator(R.drawable.btn_back)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.main_home_setting -> {
                "main_home_setting".toaste(this)
            }
            R.id.main_home_history -> {
                "main_home_history".toaste(this)
            }
            android.R.id.home -> {
                "android.R.id.home".toaste(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_home, menu)
        return true
    }
}

class SizeFragment1 : Fragment() {

    companion object {
        fun newInstance() = SizeFragment1()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.size1, container, false)
    }
}

class SizeFragment2 : Fragment() {

    companion object {
        fun newInstance() = SizeFragment2()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.size2, container, false)
    }
}