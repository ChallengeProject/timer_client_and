package kr.co.seoft.two_min.util

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.reflect.TypeToken
import kr.co.seoft.two_min.data.TimeSet

object Preferencer {

    private const val CURRENT_MEMO = "CURRENT_MEMO"
    private const val SOUND_TYPE = "BASIC_SOUND"
    private const val COUNT_DOWN = "COUNT_DOWN"
    const val SOUND_TYPE_BASIC = "SOUND_TYPE_BASIC"
    const val SOUND_TYPE_VIBRATE = "SOUND_TYPE_VIBRATE"
    const val SOUND_TYPE_SILENT = "SOUND_TYPE_SILENT"
    const val EMPTY_TIME_SET_TITLE = "EMPTY@!#&$@!"

    private const val RECENTLY_TIME_SET = "RECENTLY_TIME_SET"

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

    fun setRecentlyTimeSet(context: Context, list: List<TimeSet>) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(RECENTLY_TIME_SET, list.toJson()).apply()
    }

    fun getRecentlyTimeSet(context: Context): List<TimeSet> {
        var json = PreferenceManager.getDefaultSharedPreferences(context).getString(RECENTLY_TIME_SET, "")!!
        if (json.isEmpty()) {
            setRecentlyTimeSet(context, List(3) { TimeSet(EMPTY_TIME_SET_TITLE, emptyList()) })
            json = PreferenceManager.getDefaultSharedPreferences(context).getString(RECENTLY_TIME_SET, "")!!
        }
        return json.fromJson<List<TimeSet>>(object : TypeToken<List<TimeSet>>() {}.type)!!
    }

}