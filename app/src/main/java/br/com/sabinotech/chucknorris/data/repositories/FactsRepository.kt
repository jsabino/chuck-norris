package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single

interface FactsRepository {

    fun queryFacts(): Single<List<Fact>>

    fun changeSearchTerm(term: String)
}
