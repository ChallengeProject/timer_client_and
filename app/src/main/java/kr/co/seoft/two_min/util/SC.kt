package kr.co.seoft.two_min.util

import androidx.core.content.ContextCompat

object SC {

    fun color(rssId: Int): Int {
        return ContextCompat.getColor(App.get, rssId)
    }


}