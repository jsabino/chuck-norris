package br.com.sabinotech.chucknorris.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Search.TABLE_NAME)
data class Search(@PrimaryKey(autoGenerate = true) var id: Int?,
                  var term:String) {

    companion object {
        const val TABLE_NAME = "searches"
    }
}