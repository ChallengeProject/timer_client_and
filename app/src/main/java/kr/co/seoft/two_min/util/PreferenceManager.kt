package kr.co.seoft.two_min.util

import android.content.Context
import android.preference.PreferenceManager

object Preferencer {

    private val CURRENT_MEMO = "CURRENT_MEMO"

    fun getCurrentMemo(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(CURRENT_MEMO, "")!!
    }

    fun setCurrentMemo(context: Context, memo: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(CURRENT_MEMO, memo).apply()
    }


}