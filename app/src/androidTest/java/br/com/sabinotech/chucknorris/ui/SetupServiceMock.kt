package br.com.sabinotech.chucknorris.ui

import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.data.services.QueryFactsResponse
import br.com.sabinotech.chucknorris.data.services.QueryFactsResponseItem
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.mockito.Mockito.`when`
import retrofit2.Response

class SetupServiceMock(private val chuckNorrisService: ChuckNorrisService) {

    val queryTermForSuccess = "dev"
    val queryTermForError = "Internal server error"
    val listOfCategories = listOf("dev", "political")
    val listOfFacts = listOf(QueryFactsResponseItem("a1", "http://google.com", "Fact 1", null))

    init {
        mockResponseOfNormalQuery()

        mockResponseOfErrorInQuery()

        mockResponseOfGetCategories()
    }

    private fun mockResponseOfNormalQuery() {
        val expectedResponse1 = Response.success(QueryFactsResponse(listOfFacts))

        `when`(chuckNorrisService.queryFacts(queryTermForSuccess))
            .thenReturn(Single.just(expectedResponse1))
    }

    private fun mockResponseOfErrorInQuery() {
        val expectedResponseBody = ResponseBody.create(MediaType.get("text/plain"), "Internal server error")
        val expectedResponse2 = Response.error<QueryFactsResponse>(500, expectedResponseBody)

        `when`(chuckNorrisService.queryFacts(queryTermForError))
            .thenReturn(Single.just(expectedResponse2))
    }

    private fun mockResponseOfGetCategories() {
        `when`(chuckNorrisService.getCategories())
            .thenReturn(Single.just(listOfCategories))
    }
}