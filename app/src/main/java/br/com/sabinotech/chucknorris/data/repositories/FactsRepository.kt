package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Observable

interface FactsRepository {

    fun queryFacts(): Observable<List<Fact>>

    fun changeSearchTerm(term: String)
}
