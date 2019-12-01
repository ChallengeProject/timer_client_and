package kr.co.seoft.two_min.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes

object EditTextLenthExceedCheckUtil {


    fun checkAndBlockExceed(
        etContent: EditText,
        tvExceedNumber: TextView,
        exceedLength: Int,
        @ColorRes normalColor: Int,
        @ColorRes exceedColor: Int
    ) {
        etContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s!!.length > exceedLength) {
                    etContent.setText(s.substring(exceedLength-1, s.length - 1))
                }

                tvExceedNumber.text = s.length.toString()
                if (s.length > exceedLength-1) {
                    tvExceedNumber.setTextColor(exceedColor.color())
                    return
                }
                tvExceedNumber.setTextColor(normalColor.color())
            }
        })
    }


}