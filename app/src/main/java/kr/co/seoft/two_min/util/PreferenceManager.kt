package kr.co.seoft.two_min.util

import android.content.Context
import android.preference.PreferenceManager

object Preferencer {

    private const val CURRENT_MEMO = "CURRENT_MEMO"
    private const val SOUND_TYPE = "BASIC_SOUND"
    private const val COUNT_DOWN = "COUNT_DOWN"
    const val SOUND_TYPE_BASIC = "SOUND_TYPE_BASIC"
    const val SOUND_TYPE_VIBRATE = "SOUND_TYPE_VIBRATE"
    const val SOUND_TYPE_SILENT = "SOUND_TYPE_SILENT"

    fun getCurrentMemo(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(CURRENT_MEMO, "")!!
    }

    fun setCurrentMemo(context: Context, memo: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(CURRENT_MEMO, memo).apply()
    }

    fun getSoundType(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SOUND_TYPE, SOUND_TYPE_BASIC)!!
    }

    fun setSoundType(context: Context, soundType: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SOUND_TYPE, soundType).apply()
    }

    fun getCountDown(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(COUNT_DOWN, 3)
    }

    fun setCountDown(context: Context, countdown: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(COUNT_DOWN, countdown).apply()
    }


}