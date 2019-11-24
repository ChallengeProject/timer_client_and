package com.timer.timeset.local

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import kotlin.random.Random

class HistoryViewModel(application: Application/*, val db: AppDatabase*/) : AndroidViewModel(application) {


    private val compositeDisposable = CompositeDisposable()

    val observableHistories = MutableLiveData<List<String>>()
    val observableText = MutableLiveData<String>()

    var k = 0

    fun refreshItems() {

        // TODO set Room
//        compositeDisposable.add(
//            db.timeSetDao()
//                .selectAll()
//                .subscribe({
//                    it.e()
//                }, {
//
//                })
//        )

        observableHistories.value = listOf(
            "AA" + Random(k++).nextInt(100),
            "BB" + Random(k++).nextInt(100),
            "CC" + Random(k++).nextInt(100),
            "DD" + Random(k++).nextInt(100),
            "EE" + Random(k++).nextInt(100),
            "FF" + Random(k++).nextInt(100),
            "GG" + Random(k++).nextInt(100)
        )

        observableText.value = "텍스트 갱신"
    }

    override fun onCleared() {
        super.onCleared()
    }


}