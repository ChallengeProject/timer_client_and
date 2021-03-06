package kr.co.seoft.two_min.ui.proc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_proc_end.*
import kr.co.seoft.two_min.R
import kr.co.seoft.two_min.data.Bell
import kr.co.seoft.two_min.data.TimeSet
import kr.co.seoft.two_min.data.UseInfo
import kr.co.seoft.two_min.ui.main.MainActivity
import kr.co.seoft.two_min.util.EditTextLengthExceedCheckUtil
import kr.co.seoft.two_min.util.Preferencer
import java.util.concurrent.TimeUnit

class ProcEndActivity : AppCompatActivity() {

    val timeSet by lazy {
        intent.getParcelableExtra<TimeSet>(TIME_SET)
    }

    val useInfo by lazy {
        intent.getParcelableExtra<UseInfo>(USE_INFO)
    }


    var mediaPlayer: MediaPlayer? = null
    val compositeDisposable = CompositeDisposable()

    companion object {
        const val TIME_SET = "TIME_SET"
        const val USE_INFO = "USE_INFO"

        const val RESP_TIME_SET = "RESP_TIME_SET"
        const val RESP_USE_INFO = "RESP_USE_INFO"
        const val RESP_TYPE = "RESP_WHAT"
        const val RESP_TYPE_EXCEED = "RESP_WHAT_EXCEED"
        const val RESP_TYPE_SAVE = "RESP_WHAT_SAVE"
        const val RESP_TYPE_RESTART = "RESP_WHAT_RESTART"
        const val RESP_TYPE_EXIT = "RESP_TYPE_EXIT"

        fun startProcEndActivity(context: Context, timeSet: TimeSet, useInfo: UseInfo) {
            (context as Activity).startActivityForResult(
                Intent(context, ProcEndActivity::class.java).apply {
                    putExtra(TIME_SET, timeSet)
                    putExtra(USE_INFO, useInfo)
                },
                MainActivity.PROC_END_ACTIVITY
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc_end)

        tvTimesetTitle.text = timeSet.title
        tvDateInfo.text = "${useInfo.ymdString} / ${useInfo.startTimeString} - ${useInfo.endTimeString}"

        playSound()


        ////////////////
        // init listener

        ivTimesetEndBtn.setOnClickListener {
            Preferencer.setCurrentMemo(this, etContent.text.toString())
            finishWithMessage(RESP_TYPE_EXIT)
        }

        btSave.setOnClickListener {
            finishWithMessage(RESP_TYPE_SAVE)
        }

        btRestart.setOnClickListener {
            Preferencer.setCurrentMemo(this, etContent.text.toString())
            finishWithMessage(RESP_TYPE_RESTART)
        }

        btExceed.setOnClickListener {
            Preferencer.setCurrentMemo(this, etContent.text.toString())
            finishWithMessage(RESP_TYPE_EXCEED)
        }

        etContent.setText(Preferencer.getCurrentMemo(this))


        //TODO proc end exceed main-home 하나로 통합하기
        EditTextLengthExceedCheckUtil.checkAndBlockExceed(
            etContent,
            tvExceedNumber,
            1000,
            R.color.ux_black,
            R.color.ux_pink
        )

    }

    fun finishWithMessage(respType: String) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(RESP_TIME_SET, timeSet)
            putExtra(RESP_USE_INFO, useInfo)
            putExtra(RESP_TYPE, respType)
        })
        finish()
    }

    // same to playSound in ProcActivity
    fun playSound() {
        when (timeSet.times.last().bell.type) {
            Bell.Type.DEFAULT -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.a_3)
                mediaPlayer?.start()
                runStopSoundCount()
            }
            Bell.Type.SLIENT -> {

            }
            Bell.Type.VIBRATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                        .vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(1000)
                }
            }
            Bell.Type.USER -> {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(timeSet.times.last().bell.uriStr))
                mediaPlayer?.start()
                runStopSoundCount()
            }

        }
    }

    fun runStopSoundCount() {
        compositeDisposable.add(Observable
            .timer(3000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                stopSound()
            }
        )
    }

    fun stopSound() {
        mediaPlayer?.stop()
    }

    override fun onStop() {
        super.onStop()

        stopSound()
        compositeDisposable.clear()
    }

}
