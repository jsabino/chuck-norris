package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.domain.Category
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Maybe
import io.reactivex.Single

interface FactsRepositoryInterface {

    fun queryFacts(term: String): Single<List<Fact>>

    fun getCategories(): Maybe<List<Category>>
}
