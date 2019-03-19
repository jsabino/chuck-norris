package br.com.sabinotech.chucknorris.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.sabinotech.chucknorris.data.repositories.FactsRepository
import br.com.sabinotech.chucknorris.domain.Fact
import br.com.sabinotech.chucknorris.ui.common.NetworkState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class MainViewModel(private val repository: FactsRepository, networkState: NetworkState) : ViewModel() {

    private var facts = MutableLiveData<List<Fact>>()
    private var isLoading = MutableLiveData<Boolean>()
    private var isInternetAvailable = MutableLiveData<Boolean>()
    private var disposableSearch: Disposable? = null
    private var disposableNetwork = networkState
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { isInternetAvailable.value = it }

    fun getFacts() = facts

    fun isLoading() = isLoading

    fun isInternetAvailable() = isInternetAvailable

    fun setSearchTerm(term: String) {
        disposeSearch()

        facts.value = listOf()
        disposableSearch = repository
            .queryFacts(term)
            .map { Result.success(it) }
            .doOnSubscribe { isLoading.value = true }
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate { isLoading.value = false }
            .onErrorResumeNext { Single.just(Result.failure(it)) }
            .subscribe { result ->
                if (result.isSuccess) {
                    facts.value = result.getOrThrow()
                } else {

                }
            }
    }

    private fun disposeSearch() {
        disposableSearch?.run {
            if (!isDisposed) {
                dispose()
            }
        }
    }

    private fun disposeNetwork() {
        disposableNetwork?.run {
            if (!isDisposed) {
                dispose()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposeSearch()
        disposeNetwork()
    }
}
