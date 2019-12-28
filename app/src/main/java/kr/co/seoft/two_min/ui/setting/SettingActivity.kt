package kr.co.seoft.two_min.ui.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_setting.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.util.Preferencer
import kr.co.seoft.two_min.util.Preferencer.SOUND_TYPE_BASIC
import kr.co.seoft.two_min.util.Preferencer.SOUND_TYPE_SILENT
import kr.co.seoft.two_min.util.Preferencer.SOUND_TYPE_VIBRATE
import kr.co.seoft.two_min.util.setupActionBar

class SettingActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_setting

    companion object {
        private const val TAG = "SettingActivity"

        fun startSettingActivity(context: Context) {
            context.startActivity(
                Intent(context, SettingActivity::class.java)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initListener()

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        actSettingTvSoundType.text = "현재 : ${when (Preferencer.getSoundType(this)) {
            SOUND_TYPE_BASIC -> "기본음"
            SOUND_TYPE_VIBRATE -> "진동"
            SOUND_TYPE_SILENT -> "무음"
            else -> "기본음"
        }
        }"

        actSettingTvCountDown.text = "현재 : ${Preferencer.getCountDown(this)}초"
    }

    fun initListener() {
        actSetting1.setOnClickListener {
            NoticesActivity.startNoticesActivity(this)
        }

        actSetting2.setOnClickListener {
            SoundSettingActivity.startSoundSettingActivity(this)
        }

        actSetting3.setOnClickListener {
            CountDownSettingActivity.startCountDownSettingActivity(this)
        }

        actSetting4.setOnClickListener {
            TeamInfoActivity.startTeamInfoActivity(this)
        }

        actSetting5.setOnClickListener {
            OpenSourceActivity.startOpenSourceActivity(this)
        }
    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.___ic_back)
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
