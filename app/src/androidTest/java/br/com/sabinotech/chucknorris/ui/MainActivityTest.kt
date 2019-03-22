package br.com.sabinotech.chucknorris.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import br.com.sabinotech.chucknorris.ChuckNorrisTestApplication
import br.com.sabinotech.chucknorris.R
import br.com.sabinotech.chucknorris.data.local.AppDatabase
import br.com.sabinotech.chucknorris.data.local.model.Search
import br.com.sabinotech.chucknorris.data.services.ChuckNorrisService
import br.com.sabinotech.chucknorris.ui.adapters.TagCloudViewHolder
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest : KodeinAware {

    @get:Rule
    var activityTest = ActivityScenarioRule(MainActivity::class.java)
    private val context = InstrumentationRegistry
        .getInstrumentation()
        .targetContext
        .applicationContext as ChuckNorrisTestApplication
    override val kodein: Kodein = context.kodein.baseKodein
    override val kodeinContext = kcontext(context)
    private val chuckNorrisService: ChuckNorrisService by instance()
    private val appDatabase: AppDatabase by instance()
    private val serviceMock = SetupServiceMock(chuckNorrisService)

    @Test
    fun checkIfTheFactsFragmentHasBeenLoaded() {
        activityTest.scenario.onActivity {
            val fragments = it.supportFragmentManager.fragments

            Assert.assertEquals(1, fragments.size)
            Assert.assertEquals(FactsFragment::class.java, fragments[0]::class.java)
        }
    }

    @Test
    fun checkIfTheFactsListIsEmpty() {
        onView(ViewMatchers.withId(R.id.mainRecyclerView)).check { view, _ ->
            Assert.assertEquals(RecyclerView::class.java, view::class.java)
            val recyclerView = view as RecyclerView
            Assert.assertNull(recyclerView.adapter)
        }
    }

    @Test
    fun whenClickSearchButtonMustShowSearchFragment() {
        onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click())

        activityTest.scenario.onActivity {
            val fragments = it.supportFragmentManager.fragments

            Assert.assertEquals(1, fragments.size)
            Assert.assertEquals(SearchFragment::class.java, fragments[0]::class.java)
        }
    }

    @Test
    fun whenPressBackButtonMustReturnToFactsFragment() {
        onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click())

        Espresso.pressBack()

        activityTest.scenario.onActivity {
            val fragments = it.supportFragmentManager.fragments

            Assert.assertEquals(1, fragments.size)
            Assert.assertEquals(FactsFragment::class.java, fragments[0]::class.java)
        }
    }

    @Test
    fun checkIfTheTagsCloudIsFilled() {
        onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.searchTagCloud)).check { view, _ ->
            Assert.assertEquals(RecyclerView::class.java, view::class.java)
            val recyclerView = view as RecyclerView
            Assert.assertNotNull(recyclerView.adapter)
            Assert.assertEquals(serviceMock.listOfCategories.size, recyclerView.adapter?.itemCount)
        }
    }

    @Test
    fun checkFunctionalityOfSearchByTerm() {
        onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.searchTerm)).perform(
            ViewActions.typeText(serviceMock.queryTermForSuccess),
            ViewActions.pressImeActionButton()
        )

        assertThatFactsListIsDisplayedAndFilled()
    }

    @Test
    fun checkFunctionalityOfSearchByTag() {
        onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.searchTagCloud)).perform(
            RecyclerViewActions.actionOnItemAtPosition<TagCloudViewHolder>(
                serviceMock.listOfCategories.indexOf(serviceMock.queryTermForSuccess),
                ViewActions.click()
            )
        )

        assertThatFactsListIsDisplayedAndFilled()
    }

    @Test
    fun checkFunctionalityOfSearchByClickOnPastSearch() {
        onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click())

        appDatabase.searchDao().insert(Search(null, serviceMock.queryTermForSuccess)).subscribe()
        appDatabase.searchDao().insert(Search(null, "other category")).subscribe()

        Thread.sleep(500)

        onData(CoreMatchers.anything())
            .inAdapterView(ViewMatchers.withId(R.id.pastSearches))
            .atPosition(1)
            .perform(ViewActions.click())

        assertThatFactsListIsDisplayedAndFilled()
    }

    @Test
    fun checkIfSnackbarIsDisplayedWhenAnErrorOccurs() {
        onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.searchTerm)).perform(
            ViewActions.typeText(serviceMock.queryTermForError),
            ViewActions.pressImeActionButton()
        )

        Thread.sleep(500)

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("There was an error loading the facts")))
    }

    private fun assertThatFactsListIsDisplayedAndFilled() {
        onView(ViewMatchers.withId(R.id.mainRecyclerView)).check { view, _ ->
            Assert.assertEquals(RecyclerView::class.java, view::class.java)
            val recyclerView = view as RecyclerView

            Assert.assertNotNull(recyclerView.adapter)
            Assert.assertEquals(serviceMock.listOfFacts.size, recyclerView.adapter?.itemCount)
        }
    }
}
