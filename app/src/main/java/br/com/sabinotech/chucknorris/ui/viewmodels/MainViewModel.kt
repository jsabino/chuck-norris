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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

const val NUMBER_OF_PAST_SEARCHES = 4

class MainViewModel(
    private val repository: FactsRepositoryInterface,
    networkState: Observable<Boolean>,
    private val scheduler: Scheduler
) : ViewModel() {

    private val facts = MutableLiveData<List<Fact>>()
    private val categories = MutableLiveData<List<Category>>()
    private val pastSearches = MutableLiveData<List<String>>()
    private val isLoading = MutableLiveData<Boolean>()
    private val isInternetAvailable = MutableLiveData<Boolean>()
    private val disposables = CompositeDisposable()
    private var isCategoriesObserverInitialized = false
    private var isPastSearchesObserverInitialized = false

    init {
        initNetworkObserver(networkState)
    }

    fun getFacts() = facts

    fun getCategories(): LiveData<List<Category>> {
        initCategoriesObserver()

        return categories
    }

    fun getPastSearches(): LiveData<List<String>> {
        initPastSearchesObserver()

        return pastSearches
    }

    fun isLoading() = isLoading

    fun isInternetAvailable() = isInternetAvailable

    fun setSearchTerm(term: String) {
        facts.value = listOf()

        if (term != "") {
            saveSearch(term)

            searchFactsWithTheTerm(term)
        }
    }

    private fun searchFactsWithTheTerm(term: String) {
        val disposableSearch = repository
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
        disposables.add(disposableSearch)
    }

    private fun saveSearch(term: String) {
        val disposable = repository
            .saveSearch(term)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        disposables.add(disposable)
    }

    private fun initNetworkObserver(networkState: Observable<Boolean>) {
        disposables.add(
            networkState
                .observeOn(scheduler)
                .subscribe { isInternetAvailable.value = it })
    }

    private fun initCategoriesObserver() {
        if (isCategoriesObserverInitialized) {
            return
        }

        val disposable = repository
            .getCategories()
            .doOnSubscribe { isLoading.value = true }
            .observeOn(scheduler)
            .doAfterTerminate { isLoading.value = false }
            .subscribe({ result ->
                categories.value = result
            }, { throwable ->
                Log.d("JAYSON VM", "ERROR $throwable")
            })

        disposables.add(disposable)
        isCategoriesObserverInitialized = true
    }

    private fun initPastSearchesObserver() {
        if (isPastSearchesObserverInitialized) {
            return
        }

        val disposable = repository
            .getPastSearches(NUMBER_OF_PAST_SEARCHES)
            .doOnSubscribe { isLoading.value = true }
            .observeOn(scheduler)
            .doAfterTerminate { isLoading.value = false }
            .subscribe({ result ->
                pastSearches.value = result
            }, { throwable ->
                Log.d("JAYSON VM", "ERROR $throwable")
            })

        disposables.add(disposable)
        isPastSearchesObserverInitialized = true
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}
