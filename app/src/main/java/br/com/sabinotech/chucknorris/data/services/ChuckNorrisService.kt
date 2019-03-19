package br.com.sabinotech.chucknorris.data.services

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ChuckNorrisService {

    @GET("jokes/search")
    fun queryFacts(@Query("query") query: String): Single<Response<QueryFactsResponse>>

    @GET("jokes/categories")
    fun getCategories(): Single<List<String>>
}

data class QueryFactsResponse(val result: List<QueryFactsResponseItem>)

data class QueryFactsResponseItem(
    val id: String,
    val url: String,
    @SerializedName("value") val text: String,
    val category: List<String>?
)
