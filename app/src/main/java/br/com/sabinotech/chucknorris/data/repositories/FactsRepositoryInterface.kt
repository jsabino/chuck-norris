package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.domain.Category
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

interface FactsRepositoryInterface {

    fun queryFacts(term: String): Single<List<Fact>>

    fun getCategories(): Maybe<List<Category>>

    fun saveSearch(term: String): Completable

    fun getPastSearches(quantity: Int): Observable<List<String>>
}
