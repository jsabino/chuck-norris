package br.com.sabinotech.chucknorris.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.sabinotech.chucknorris.data.local.model.Category
import io.reactivex.Single

@Dao
interface CategoryDao {

    @Query("SELECT * FROM " + Category.TABLE_NAME)
    fun getAll(): Single<List<Category>>

    @Insert
    fun insertList(categories: List<Category>)
}
