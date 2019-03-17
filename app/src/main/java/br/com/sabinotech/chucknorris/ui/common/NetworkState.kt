package br.com.sabinotech.chucknorris.ui.common

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NetworkState: Observable<Boolean>() {

    private var isConnected = true
    private val observable by lazy {
        ReactiveNetwork
            .observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
    }

    init {
        observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isConnected = it
            }
    }

    fun isInternetAvailable(): Boolean {
        return isConnected
    }

    override fun subscribeActual(observer: Observer<in Boolean>) {
        observable.subscribe(observer)
    }
}
