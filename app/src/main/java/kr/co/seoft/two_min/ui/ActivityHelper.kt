package kr.co.seoft.two_min.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class ActivityHelper : AppCompatActivity() {

    abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
    }
}