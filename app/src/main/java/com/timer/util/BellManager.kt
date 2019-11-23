package com.timer.se_util

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri

object BellManager {

    fun getBasicBells(context: Context): List<Pair<String, Uri>> {
        val manager = RingtoneManager(context).apply {
            setType(RingtoneManager.TYPE_RINGTONE)
        }
        val cursor = manager.cursor

        val bells = mutableListOf<Pair<String, Uri>>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val ringtoneURI = manager.getRingtoneUri(cursor.position)

//            "title $title ringtoneURI $ringtoneURI".i()
            bells.add(Pair(title, ringtoneURI))
        }

        return bells
    }


}