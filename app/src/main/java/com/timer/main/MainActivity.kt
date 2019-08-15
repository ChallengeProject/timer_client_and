package com.timer.main

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.timer.R

class MainActivity : AppCompatActivity() {
    private val mainFragment = MainFragment()
//    private val procTimeSetFragment = ProcTimeSetFragment() // TODO need to remove

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val window = (this as Activity).window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.WHITE
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        addMainFragment()
    }

    private fun addMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, mainFragment) // TODO need to changed
        transaction.commit()
    }
}

