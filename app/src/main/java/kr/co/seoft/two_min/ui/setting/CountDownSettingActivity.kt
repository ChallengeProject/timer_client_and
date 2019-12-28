package kr.co.seoft.two_min.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_count_down_setting.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.util.Preferencer
import kr.co.seoft.two_min.util.setupActionBar

class CountDownSettingActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_count_down_setting

    companion object {
        private const val TAG = "CountDownSettingActivity"

        fun startCountDownSettingActivity(context: Context) {
            context.startActivity(
                Intent(context, CountDownSettingActivity::class.java)
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initListener()

        setCountDown(Preferencer.getCountDown(this))

    }

    fun initListener() {

        actCountDownSettingLl10.setOnClickListener {
            setCountDown(10)
        }

        actCountDownSettingLl5.setOnClickListener {
            setCountDown(5)
        }

        actCountDownSettingLl3.setOnClickListener {
            setCountDown(3)
        }

        actCountDownSettingLl2.setOnClickListener {
            setCountDown(2)
        }

        actCountDownSettingLl1.setOnClickListener {
            setCountDown(1)
        }

        actCountDownSettingLl0.setOnClickListener {
            setCountDown(0)
        }


    }

    fun setCountDown(second: Int) {

        actCountDownSettingIv10.visibility = View.INVISIBLE
        actCountDownSettingIv5.visibility = View.INVISIBLE
        actCountDownSettingIv3.visibility = View.INVISIBLE
        actCountDownSettingIv2.visibility = View.INVISIBLE
        actCountDownSettingIv1.visibility = View.INVISIBLE
        actCountDownSettingIv0.visibility = View.INVISIBLE

        when (second) {
            10 -> actCountDownSettingIv10.visibility = View.VISIBLE
            5 -> actCountDownSettingIv5.visibility = View.VISIBLE
            3 -> actCountDownSettingIv3.visibility = View.VISIBLE
            2 -> actCountDownSettingIv2.visibility = View.VISIBLE
            1 -> actCountDownSettingIv1.visibility = View.VISIBLE
            0 -> actCountDownSettingIv0.visibility = View.VISIBLE
        }

        Preferencer.setCountDown(this, second)

    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.___ic_back)
            setTitle("카운트다운")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
