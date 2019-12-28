package kr.co.seoft.two_min.ui.setting

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sound_setting.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.ui.ActivityHelper
import kr.co.seoft.two_min.util.Preferencer
import kr.co.seoft.two_min.util.setupActionBar
import java.util.concurrent.TimeUnit

class SoundSettingActivity : ActivityHelper() {

    override val layoutResourceId = R.layout.activity_sound_setting

    companion object {
        private const val TAG = "SoundSettingActivity"

        fun startSoundSettingActivity(context: Context) {
            context.startActivity(
                Intent(context, SoundSettingActivity::class.java)
            )
        }
    }

    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initListener()

        setSound(Preferencer.getSoundType(this))
        mediaPlayer = MediaPlayer.create(this, R.raw.a_3)
    }

    fun setSound(soundType: String) {
        actSoundSettingIvBasic.visibility = View.INVISIBLE
        actSoundSettingIvVibrate.visibility = View.INVISIBLE
        actSoundSettingIvSilent.visibility = View.INVISIBLE

        when (soundType) {
            Preferencer.SOUND_TYPE_BASIC -> {
                actSoundSettingIvBasic.visibility = View.VISIBLE
                Preferencer.setSoundType(this, Preferencer.SOUND_TYPE_BASIC)
            }
            Preferencer.SOUND_TYPE_VIBRATE -> {
                actSoundSettingIvVibrate.visibility = View.VISIBLE
                Preferencer.setSoundType(this, Preferencer.SOUND_TYPE_VIBRATE)
            }
            Preferencer.SOUND_TYPE_SILENT -> {
                actSoundSettingIvSilent.visibility = View.VISIBLE
                Preferencer.setSoundType(this, Preferencer.SOUND_TYPE_SILENT)
            }
        }

    }


    fun initListener() {

        actSoundSettingTvPlayBtn.setOnClickListener {
            mediaPlayer.start()
            runStopSoundCount()
        }

        actSoundSettingLlBasic.setOnClickListener {
            setSound(Preferencer.SOUND_TYPE_BASIC)
        }

        actSoundSettingLlVibrate.setOnClickListener {
            setSound(Preferencer.SOUND_TYPE_VIBRATE)
        }

        actSoundSettingLlSilent.setOnClickListener {
            setSound(Preferencer.SOUND_TYPE_SILENT)
        }
    }

    fun runStopSoundCount() {
        val obsever = Observable
            .timer(3000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                stopSound()
            }
    }

    fun stopSound() {
        mediaPlayer.stop()
    }

    private fun initToolbar() {
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable._ic_back)
            setTitle("기본 사운드 설정")
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
