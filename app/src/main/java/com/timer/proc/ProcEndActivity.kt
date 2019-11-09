package com.timer.proc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timer.R

class ProcEndActivity : AppCompatActivity() {


    companion object {
        val TIMES = "TIMES"
        private val READY_SEC = "READY_SEC"

        fun startProcEndActivity(context: Context) {
            context.startActivity(Intent(context, ProcEndActivity::class.java).apply {
            })
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proc_end)



    }



}
