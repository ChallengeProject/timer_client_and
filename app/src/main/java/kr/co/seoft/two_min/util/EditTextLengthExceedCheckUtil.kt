package kr.co.seoft.two_min.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes

object EditTextLengthExceedCheckUtil {


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

                var nonNullString = s?.toString() ?: return

                if (nonNullString.toByteArray().size > exceedLength) {
                    nonNullString = nonNullString.substring(0, nonNullString.length - 2)
                    etContent.setText(nonNullString)
                    etContent.setSelection(etContent.text.length)
                }

                tvExceedNumber.text = nonNullString.toByteArray().size.toString()

                if (nonNullString.toByteArray().size > exceedLength - 1) {
                    tvExceedNumber.setTextColor(exceedColor.color())
                    return
                }
                tvExceedNumber.setTextColor(normalColor.color())
            }
        })
    }


}