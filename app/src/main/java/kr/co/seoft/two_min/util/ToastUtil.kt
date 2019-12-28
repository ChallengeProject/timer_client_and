package kr.co.seoft.two_min.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.co.seoft.two_min.R
import java.util.concurrent.TimeUnit

object ToastUtil {

    fun showToast(activity: Activity, content: String) {

        val toastLayout = activity.layoutInflater.inflate(R.layout.toast, (activity.findViewById(R.id.toastLlRoot)))
        val toastLlIn = toastLayout.findViewById<LinearLayout>(R.id.toastLlIn)
        val toastTextView = toastLlIn.findViewById<TextView>(R.id.toastTvContent)
        toastTextView.text = content

        Toast(activity.baseContext).apply {
            setGravity(Gravity.BOTTOM, 0, 15.dpToPx())
            duration = Toast.LENGTH_LONG
            view = toastLayout

        }.show()
    }

    fun showToastTask(activity: Activity, content: String, buttonText: String, cb: () -> Unit) {
        Handler().postDelayed({
            ToastPopupView(activity, content, buttonText, cb)
        },100)
    }


}

class ToastPopupView(val context: Context, val content: String, val buttonText: String, val cb: () -> Unit) {

    var popupWindow: PopupWindow?

    var compositeDisposable = CompositeDisposable()

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.toast_task, null)
        val toastTaskLlRoot = popupView.findViewById<LinearLayout>(R.id.toastTaskLlRoot)
        val toastTaskLlIn = toastTaskLlRoot.findViewById<LinearLayout>(R.id.toastTaskLlIn)
        val toastTaskTvButton = toastTaskLlIn.findViewById<TextView>(R.id.toastTaskTvButton)
        val toastTaskTvContent = toastTaskLlIn.findViewById<TextView>(R.id.toastTaskTvContent)

        toastTaskTvButton.text = content
        toastTaskTvContent.text = buttonText

        popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        popupWindow?.showAtLocation(popupView, Gravity.BOTTOM, 0, 0)

        toastTaskTvButton.setOnClickListener {
            cb.invoke()
            popupWindow?.dismiss()
            popupWindow = null
        }

        compositeDisposable.add(
            Single.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    executeWhenSubscribe()
                }, {

                })
        )
    }

    private fun executeWhenSubscribe() {
        popupWindow?.dismiss()
        popupWindow = null
        compositeDisposable.clear()
    }


}