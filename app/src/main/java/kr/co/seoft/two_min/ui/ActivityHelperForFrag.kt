package kr.co.seoft.two_min.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.co.seoft.two_min.data.AppDatabase
import kr.co.seoft.two_min.data.TimeSet

/**
 * MainActivity - homeFragment의 구조로 개발했는데,
 * EditActivity - homeFragment로 재사용 할 필요성이 생겨 (재사용 안하고 새로만들기에 엮여있는 공통부분이 너무 많음)
 *
 * 이미 이전에 homeFrag에서 mainAct에 디펜던시가 너무생겨 해당부분을 super로 올려 기존 호출은 유지하되,
 * 새로운 EditAct도 사용하기 위해 만들어진 abstract class
 */
abstract class ActivityHelperForFrag : AppCompatActivity() {

    abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
    }

    val db by lazy { AppDatabase.getDatabase(this) }

    abstract fun startProc(timeSet: TimeSet)
    abstract fun startSave(timeSet: TimeSet)
    abstract fun setShowBottomButtons(isShow: Boolean)
    abstract fun setLockViewpager(isLock: Boolean)
    abstract fun setTransparentToolbarAndBottoms(isTransparent: Boolean)
    abstract fun showToastMessage(content: String)
    abstract fun showToastMessage(content: String, buttonText: String, cb: () -> Unit)
    abstract fun showSelectorDialog(content: String, btn1Text: String, btn2Text: String, btn1Cb: () -> Unit, btn2Cb: () -> Unit)
}