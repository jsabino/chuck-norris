package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.data.services.QueryFactsResponse
import br.com.sabinotech.chucknorris.data.services.QueryFactsResponseItem
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response

class FactsRemoteRepositoryTest {

    private val chuckNorrisService by lazy { mock(ChuckNorrisService::class.java) }
    private val repository by lazy { FactsRemoteRepository(chuckNorrisService) }

    @Test
    fun `when no query string is set return empty list`() {
        repository
            .queryFacts()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertResult(listOf())
    }

    @Test
    fun `when query string is 'dev' return two facts`() {
        val queryFactsResponse = QueryFactsResponse(
            listOf(
                QueryFactsResponseItem("a2", "http://google.com", "Dev Joke 1", null),
                QueryFactsResponseItem("b3", "http://google2.com", "Dev Joke 2", listOf("DEV"))
            )
        )

        val expectedFacts = queryFactsResponse.result.map { Fact(it.id, it.url, it.text, it.category?.first()) }

        `when`(chuckNorrisService.queryFacts("dev"))
            .thenReturn(Single.just(Response.success(queryFactsResponse)))

        repository.changeSearchTerm("dev")

        val facts = repository.queryFacts().blockingGet()
        assertThat(facts).isEqualTo(expectedFacts)
    }
}
