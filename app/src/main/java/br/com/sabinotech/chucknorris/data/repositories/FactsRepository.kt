package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.data.local.AppDatabase
import br.com.sabinotech.chucknorris.data.local.model.Search
import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.domain.Category
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FactsRepository(
    private val service: ChuckNorrisService,
    private val database: AppDatabase
) : FactsRepositoryInterface {

    override fun queryFacts(term: String): Single<List<Fact>> {
        if (term == "") {
            return Single.just(listOf())
        }

        return service
            .queryFacts(term)
            .subscribeOn(Schedulers.io())
            .map {
                it.body()?.let { response ->
                    response.result.map { item ->
                        Fact(item.id, item.url, item.text, item.category?.first())
                    }
                }
            }
    }

    override fun getCategories(): Maybe<List<Category>> {
        val local = getCategoriesLocal()

        val remote = getCategoriesRemote()
            .doOnSuccess { insertCategoriesIntoDatabase(it) }

        return Maybe
            .concat(local, remote)
            .subscribeOn(Schedulers.io())
            .firstElement()
    }

    override fun saveSearch(term: String) = database
        .searchDao()
        .insert(Search(id = null, term = term))
        .subscribeOn(Schedulers.io())

    override fun getPastSearches(quantity: Int): Observable<List<String>> = database
        .searchDao()
        .getLast(quantity)
        .subscribeOn(Schedulers.io())

    private fun getCategoriesLocal() = database
        .categoryDao()
        .getAll()
        .filter { !it.isEmpty() }
        .map { list ->
            list.map { Category(it.name) }
        }

    private fun getCategoriesRemote() = service
        .getCategories()
        .filter { !it.isEmpty() }
        .map { list ->
            list.map { Category(it) }
        }

    private fun insertCategoriesIntoDatabase(categories: List<Category>) {
        database
            .categoryDao()
            .insertList(categories.map {
                br.com.sabinotech.chucknorris.data.local.model.Category(it.name)
            })
    }
}
