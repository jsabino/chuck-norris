package br.com.sabinotech.chucknorris.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.sabinotech.chucknorris.data.local.dao.CategoryDao
import br.com.sabinotech.chucknorris.data.local.dao.SearchDao
import br.com.sabinotech.chucknorris.data.local.model.Category
import br.com.sabinotech.chucknorris.data.local.model.Search

@Database(entities = [Category::class, Search::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun searchDao(): SearchDao
}
