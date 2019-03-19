package br.com.sabinotech.chucknorris.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.sabinotech.chucknorris.data.local.dao.CategoryDao
import br.com.sabinotech.chucknorris.data.local.model.Category

@Database(entities = [Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
}
