package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FactsRemoteRepository(private val service: ChuckNorrisService) : FactsRepository {

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
}