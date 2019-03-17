package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FactsRemoteRepository(private val service: ChuckNorrisService) : FactsRepository {

    private var query: String = ""

    override fun queryFacts(): Single<List<Fact>> {
        if (query == "") {
            return Single.just(listOf())
        }

        return service
            .queryFacts(query)
            .subscribeOn(Schedulers.io())
            .map {
                it.body()?.let { response ->
                    response.result.map { item ->
                        Fact(item.id, item.url, item.text, item.category?.first())
                    }
                }
            }
    }

    override fun changeSearchTerm(term: String) {
        query = term
    }
}