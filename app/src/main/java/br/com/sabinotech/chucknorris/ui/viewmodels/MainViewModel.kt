package br.com.sabinotech.chucknorris.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.sabinotech.chucknorris.data.repositories.FactsRepositoryInterface
import br.com.sabinotech.chucknorris.domain.Category
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable

class MainViewModel(
    private val repository: FactsRepositoryInterface,
    networkState: Observable<Boolean>,
    private val scheduler: Scheduler
) : ViewModel() {

    private var facts = MutableLiveData<List<Fact>>()
    private var categories = MutableLiveData<List<Category>>()
    private var isLoading = MutableLiveData<Boolean>()
    private var isInternetAvailable = MutableLiveData<Boolean>()
    private var disposableSearch: Disposable? = null
    private var disposableCategories: Disposable? = null
    private var disposableNetwork = networkState
        .observeOn(scheduler)
        .subscribe { isInternetAvailable.value = it }

    fun getFacts() = facts

    fun getCategories(): LiveData<List<Category>> {
        if (disposableCategories == null) {
            disposableCategories = repository
                .getCategories()
                .doOnSubscribe { isLoading.value = true }
                .observeOn(scheduler)
                .doAfterTerminate { isLoading.value = false }
                .subscribe({ result ->
                        categories.value = result
                    }, { throwable ->
                        Log.d("JAYSON VM", "ERROR $throwable")
                    })
        }

        return categories
    }

    fun isLoading() = isLoading

    fun isInternetAvailable() = isInternetAvailable

    fun setSearchTerm(term: String) {
        disposeSearch()

        facts.value = listOf()
        disposableSearch = repository
            .queryFacts(term)
            .map { Result.success(it) }
            .doOnSubscribe { isLoading.value = true }
            .observeOn(scheduler)
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

    private fun disposeCategories() {
        disposableCategories?.run {
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
        disposeCategories()
        disposeNetwork()
    }
}
