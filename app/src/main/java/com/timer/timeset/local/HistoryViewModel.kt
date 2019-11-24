package com.timer.timeset.local

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.timer.data.AppDatabase
import com.timer.data.History
import com.timer.util.VMHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class HistoryViewModel(application: Application, val db: AppDatabase) : VMHelper(application) {

    val observableHistories = MutableLiveData<List<History>>()
    val observableText = MutableLiveData<String>()

    fun refreshItems() {

        addDisposable(
            db.historyDao()
                .selectAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    observableHistories.value = it
                }, {
                    it.printStackTrace()
                })
        )

        observableText.value = "텍스트 갱신 : ${Random(1234).nextInt()}"
    }


}