package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single

interface FactsRepository {

    fun queryFacts(term: String): Single<List<Fact>>
}
