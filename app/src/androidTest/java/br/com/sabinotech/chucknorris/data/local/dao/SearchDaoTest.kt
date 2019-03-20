package br.com.sabinotech.chucknorris.data.local.dao

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import br.com.sabinotech.chucknorris.data.local.AppDatabase
import br.com.sabinotech.chucknorris.data.local.model.Search
import org.junit.After
import org.junit.Test
import java.io.IOException

class SearchDaoTest {

    private val applicationContext by lazy { InstrumentationRegistry.getInstrumentation().context }
    private val database by lazy {
        Room
            .inMemoryDatabaseBuilder(
                applicationContext,
                AppDatabase::class.java
            )
            .build()
    }
    private val searchDao by lazy { database.searchDao() }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun getLast_mustRespectTheLimitOfRequestedItems() {
        listOf("DEV", "POLITICAL", "TEST").map {
            searchDao.insert(Search(null, it)).test().await().assertComplete()
        }

        searchDao.getLast(2)
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(listOf("TEST", "POLITICAL"))

        searchDao.getLast(5)
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(listOf("TEST", "POLITICAL", "DEV"))
    }

    @Test
    fun getLast_mustSortByTheLastSearchFirst() {
        listOf("1", "2", "3").map {
            searchDao.insert(Search(null, it)).test().await().assertComplete()
        }

        searchDao.getLast(5)
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(listOf("3", "2", "1"))
    }

    @Test
    fun getLast_mustReturnOnlyDistinctValues() {
        val listWithDuplicatedValues = listOf("DEV", "POLITICAL", "DEV")

        listWithDuplicatedValues.map {
            searchDao.insert(Search(null, it)).test().await().assertComplete()
        }

        searchDao.getLast(3)
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(listOf("DEV", "POLITICAL"))
    }
}
