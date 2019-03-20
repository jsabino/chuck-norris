package br.com.sabinotech.chucknorris.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.sabinotech.chucknorris.data.local.model.Search
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface SearchDao {

    @Query("SELECT DISTINCT term FROM ${Search.TABLE_NAME} ORDER BY id DESC LIMIT :quantity")
    fun getLast(quantity: Int): Observable<List<String>>

    @Insert
    fun insert(search: Search): Completable
}
