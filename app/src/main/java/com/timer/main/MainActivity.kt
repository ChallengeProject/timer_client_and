package com.timer.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timer.R

class MainActivity : AppCompatActivity() {
    private val mainFragment = MainFragment()
    val mainViewModel = MainViewModel(mainFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addMainFragment()
    }

    private fun addMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.main, mainFragment)
        transaction.commit()
    }
}

