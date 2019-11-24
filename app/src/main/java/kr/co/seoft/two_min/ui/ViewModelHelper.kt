package kr.co.seoft.two_min.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class ViewModelHelper : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }


    fun create(): ViewModelFactory {
        return ViewModelFactory(this)
    }
}

class ViewModelFactory(val vm: ViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = vm as T
}
