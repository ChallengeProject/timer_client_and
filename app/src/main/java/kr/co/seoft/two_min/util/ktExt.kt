package kr.co.seoft.two_min.util

import android.content.Context
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData


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

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}
