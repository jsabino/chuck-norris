package br.com.sabinotech.chucknorris.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.sabinotech.chucknorris.ui.common.NetworkState
import io.reactivex.android.schedulers.AndroidSchedulers
import org.kodein.di.DKodein
import org.kodein.di.generic.instance
import org.kodein.di.newInstance

class ViewModelFactory(private val injector: DKodein) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return injector.newInstance { MainViewModel(instance(), NetworkState(), AndroidSchedulers.mainThread()) } as T
        }

        return modelClass.newInstance()
    }
}