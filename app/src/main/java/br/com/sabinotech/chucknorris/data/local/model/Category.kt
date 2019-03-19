package br.com.sabinotech.chucknorris.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Category.TABLE_NAME)
data class Category (@PrimaryKey var name:String) {

    companion object {
        const val TABLE_NAME = "categories"
    }
}
