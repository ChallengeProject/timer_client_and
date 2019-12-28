package kr.co.seoft.two_min.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.TextView
import kr.co.seoft.two_min.R

class SelectorDialog(
    val context: Context, val content: String,
    val btn1Text: String, val btn2Text: String,
    val btn1Cb: () -> Unit, val btn2Cb: () -> Unit
) {

    var dialog: Dialog

    init {

        dialog = Dialog(context).apply {

            window.attributes = WindowManager.LayoutParams().apply {
                flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
                dimAmount = 0.8F
            }

            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setContentView(R.layout.dialog_selector)

            var tvContent = findViewById<TextView>(R.id.dialogSelectorTvContent)
            var tvBtn1 = findViewById<TextView>(R.id.dialogSelectorTvBtn1)
            var tvBtn2 = findViewById<TextView>(R.id.dialogSelectorTvBtn2)
            tvContent.text = content
            tvBtn1.text = btn1Text
            tvBtn2.text = btn2Text

            tvBtn1.setOnClickListener {
                btn1Cb.invoke()
                dismiss()
            }

            tvBtn2.setOnClickListener {
                btn2Cb.invoke()
                dismiss()
            }

        }

        dialog.show()
    }

}