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

        actSettingTvSoundType.text = "${getString(R.string.current)} : ${when (Preferencer.getSoundType(this)) {
            SOUND_TYPE_BASIC -> getString(R.string.defaultt)
            SOUND_TYPE_VIBRATE -> getString(R.string.vibration)
            SOUND_TYPE_SILENT -> getString(R.string.slient)
            else -> getString(R.string.defaultt)
        }
        }"

        actSettingTvCountDown.text = "${getString(R.string.current)} : ${Preferencer.getCountDown(this)}${getString(R.string.seconds)}"
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
            setHomeAsUpIndicator(R.drawable._ic_back)
            setTitle(getString(R.string.setting))
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
