package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single

class FactsTestRepository : FactsRepository {

    private var query: String = ""

    override fun queryFacts(): Single<List<Fact>> = Single.create {
        if (query == "") {
            it.onSuccess(listOf())
        } else {
            it.onSuccess(
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
    }

    override fun changeSearchTerm(term: String) {
        query = term
    }
}
