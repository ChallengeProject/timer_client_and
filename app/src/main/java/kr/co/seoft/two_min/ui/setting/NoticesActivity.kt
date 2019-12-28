package kr.co.seoft.two_min.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.util.setupActionBar

class NoticesActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_notices

    companion object {
        private const val TAG = "NoticesActivity"

        fun startNoticesActivity(context: Context) {
            context.startActivity(
                Intent(context, NoticesActivity::class.java)
            )
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initListener()

    }

    fun initListener() {

    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable._ic_back)
            setTitle("설정")
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
