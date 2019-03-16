package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Observable

class FactsTestRepository : FactsRepository {

    private var query: String = ""

    override fun queryFacts(): Observable<List<Fact>> = Observable.create {
        if (query == "") {
            it.onNext(listOf())
        } else {
            it.onNext(
                listOf(
                    Fact(
                        "1",
                        "http://google.com",
                        "The band Panic At The Disco got their name when Chuck Norris visited Studio 54.",
                        null
                    ),
                    Fact(
                        "2",
                        "http://google.com",
                        "Chuck Norris likes lots of honey on his Texas Toast. That's why he keeps 27 killer bee hives in his livingroom.",
                        "TEST"
                    )
                )
            )
        }

        it.onComplete()
    }

    override fun changeSearchTerm(term: String) {
        query = term
    }
}
