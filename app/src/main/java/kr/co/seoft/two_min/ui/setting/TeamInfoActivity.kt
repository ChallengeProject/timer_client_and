package kr.co.seoft.two_min.ui.setting

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_team_info.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.util.ToastUtil
import kr.co.seoft.two_min.util.setupActionBar


class TeamInfoActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_team_info

    companion object {
        private const val TAG = "TeamInfoActivity"

        fun startTeamInfoActivity(context: Context) {
            context.startActivity(
                Intent(context, TeamInfoActivity::class.java)
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initListener()

    }

    fun initListener() {

        actTeamInfoTvCopy.setOnClickListener {
            val myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip = ClipData.newPlainText("text", "ask2min@gmail.com")
            myClipboard.setPrimaryClip(myClip)
            ToastUtil.showToast(this,getString(R.string.copy_to_clipboard))
        }
    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable._ic_back)
            setTitle(getString(R.string.team_info))
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
