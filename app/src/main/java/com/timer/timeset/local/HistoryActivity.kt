package com.timer.timeset.local

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timer.R
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    companion object {

        const val ACTION_CONTENT = "ACTION_CONTENT"

        fun startActivity(context: Context, content: String) {
            context.startActivity(
                Intent(context, HistoryActivity::class.java).apply {
                    putExtra(ACTION_CONTENT, content)
                }
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        activityHistoryTvContent.text = intent.getStringExtra(ACTION_CONTENT)


    }


}
