package kr.co.seoft.two_min.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun <T : Any> LiveData<T>.get(): T = value!!

fun String.toaste(context: Context, tag: String = "", duration: Int = Toast.LENGTH_LONG): Toast {
    this.e("$tag#$#")
    return Toast.makeText(context, this, duration).apply { show() }
}

fun String.toast(context: Context,duration: Int = Toast.LENGTH_LONG): Toast {
    return Toast.makeText(context, this, duration).apply { show() }
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

// Any extentions
fun Any.e(tag: String = "") {
    Log.e("$tag#$#", this.toString())
}

fun Any.i(tag: String = "") {
    Log.i("$tag#$#", this.toString())
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}


fun Long.toTimeStr(): String {
    return String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(
                this
            )
        ),
        TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                this
            )
        )
    )
}

/**
 * for easy convenient convert, milliseconds is long, need to multiply 1000
 */
fun Int.x1000L(): Long {
    return this * 1000L
}

fun String.toast(duration: Int = Toast.LENGTH_LONG): Toast {
    return Toast.makeText(App.get, this, duration).apply { show() }
}

fun Int.pxToDp(): Int {
    return (this / Resources.getSystem().displayMetrics.density).toInt()
}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Int.dimen(context: Context): Int {
    return context.resources.getDimension(this).toInt()
}

fun Int.toFormattingString(): String {
    var hour = this / 3600
    var min = this / 60 % 60
    var second_ = this % 60

    return "${if (hour < 10) "0$hour" else hour}:${if (min < 10) "0$min" else min}:${if (second_ < 10) "0$second_" else second_}"
}

fun Int.toDrawable(context: Context): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        context.getDrawable(this)
    } else {
        context.resources.getDrawable(this)
    }
}

fun Int.toEndTimeStrAfterSec(): String {
    val format = SimpleDateFormat("hh:mm a", Locale.US)

    val gCalendar = GregorianCalendar()
    format.calendar = gCalendar
    gCalendar.add(Calendar.SECOND, this)
    return format.format(gCalendar.time)
}