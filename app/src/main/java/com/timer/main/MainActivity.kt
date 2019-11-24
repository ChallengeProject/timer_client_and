package com.timer.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timer.R
import com.timer.proc.ProcActivity
import com.timer.proc.ProcEndActivity
import com.timer.proc.ProcExceedActivity
import com.timer.se_data.TimeSet
import com.timer.se_util.i

class MainActivity : AppCompatActivity() {

    companion object {
        const val PROC_ACTIVITY = 1111
        const val PROC_END_ACTIVITY = 1112
        const val PROC_EXCEED_ACTIVITY = 1113
    }

    private val mainFragment = MainFragment()
    val mainViewModel = MainViewModel(mainFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addMainFragment()


/////////////// 임시 데이터 생성, 원래는 뷰모델에 있을코드
//        Single.just(AppDatabase.getInstance(this))
//            .subscribeOn(Schedulers.io())
//            .subscribe { db->
//                db.historyDao().insert(History(content = "aaaaaaaaaaaaaaaa"))
//                db.historyDao().insert(History(content = "bbbbbbbbbbbbbbbb"))
//                db.historyDao().insert(History(content = "ccccccccccccccc"))
//                db.historyDao().insert(History(content = "ddddddddddddddd"))
//                db.historyDao().insert(History(content = "eeeeeeeeeeeeee"))
//                db.historyDao().insert(History(content = "fffffffffffffffff"))
//                db.historyDao().insert(History(content = "ggggggggggggggggg"))
//                db.historyDao().insert(History(content = "hhhhhhhhhhhhhhhhh"))
//                db.historyDao().insert(History(content = "iiiiiiiiiiiiiiiiiii"))
//            }
    }

    private fun addMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.keypadContainer, mainFragment)
        transaction.commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        "onActivityResult111".i()

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PROC_ACTIVITY -> {
                    "PROC_ACTIVITY in Main".i()
                    ProcEndActivity.startProcEndActivity(
                        this,
                        data.getParcelableExtra(ProcActivity.RESP_TIME_SET),
                        data.getParcelableExtra(ProcActivity.RESP_USE_INFO)
                    )
                }
                PROC_END_ACTIVITY -> {
                    "PROC_END_ACTIVITY in Main".i()
                    val timeSet = data.getParcelableExtra<TimeSet>(ProcEndActivity.RESP_TIME_SET)
                    when (data.getStringExtra(ProcEndActivity.RESP_TYPE)) {

                        ProcEndActivity.RESP_TYPE_EXCEED -> {
                            ProcExceedActivity.startProcExceedActivity(this, timeSet)
                        }
                        ProcEndActivity.RESP_TYPE_RESTART -> {
                            ProcActivity.startProcActivity(this, timeSet)
                        }
                        ProcEndActivity.RESP_TYPE_SAVE -> {
                            // TODO 저장 구체화되면 ㄱㄱ
                        }
                    }
                }
                PROC_EXCEED_ACTIVITY -> {
                    "PROC_EXCEED_ACTIVITY in Main".i()
                }

            }
        }


    }

}

