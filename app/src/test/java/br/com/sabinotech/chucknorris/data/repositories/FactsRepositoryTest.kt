package br.com.sabinotech.chucknorris.data.repositories

import br.com.sabinotech.chucknorris.data.local.AppDatabase
import br.com.sabinotech.chucknorris.data.local.dao.CategoryDao
import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.data.services.QueryFactsResponse
import br.com.sabinotech.chucknorris.data.services.QueryFactsResponseItem
import br.com.sabinotech.chucknorris.domain.Category
import br.com.sabinotech.chucknorris.domain.Fact
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response

class FactsRepositoryTest {

    private val service by lazy { mock(ChuckNorrisService::class.java) }
    private val database by lazy { mock(AppDatabase::class.java) }
    private val categoryDao by lazy { mock(CategoryDao::class.java) }
    private val repository by lazy { FactsRepository(service, database) }

    @Test
    fun `when no query string is set return empty list`() {
        repository
            .queryFacts("")
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

        `when`(service.queryFacts("dev"))
            .thenReturn(Single.just(Response.success(queryFactsResponse)))

        repository
            .queryFacts("dev")
            .test()
            .await()
            .assertNoErrors()
            .assertComplete()
            .assertResult(expectedFacts)
    }

    @Test
    fun `when the database has categories return them from it`() {
        `when`(service.getCategories()).thenReturn(Single.just(listOf()))
        `when`(database.categoryDao()).thenReturn(categoryDao)
        `when`(categoryDao.getAll()).thenReturn(
            Single.just(
                listOf(
                    br.com.sabinotech.chucknorris.data.local.model.Category(
                        "DEV"
                    )
                )
            )
        )

        repository
            .getCategories()
            .test()
            .await()
            .assertNoErrors()
            .assertComplete()
            .assertResult(listOf(Category("DEV")))
    }

    @Test
    fun `when the database is empty return categories from api and save to database`() {
        `when`(service.getCategories()).thenReturn(Single.just(listOf("DEV")))
        `when`(database.categoryDao()).thenReturn(categoryDao)
        `when`(categoryDao.getAll()).thenReturn(Single.just(listOf()))

        var isListInserted = false
        val expectedInsert = listOf(br.com.sabinotech.chucknorris.data.local.model.Category("DEV"))
        `when`(categoryDao.insertList(expectedInsert)).then {
            isListInserted = true
            Mockito.any()
        }

        repository
            .getCategories()
            .test()
            .await()
            .assertNoErrors()
            .assertComplete()
            .assertResult(listOf(Category("DEV")))

        Assert.assertEquals(true, isListInserted)
    }
}
