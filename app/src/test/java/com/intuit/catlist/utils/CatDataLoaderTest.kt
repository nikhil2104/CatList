package com.intuit.catlist.utils

import android.content.SharedPreferences
import com.intuit.catlist.model.Breed
import com.intuit.catlist.model.Cat
import com.intuit.catlist.network.Endpoint
import com.intuit.catlist.persist.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class CatDataLoaderTest {
    private val mCoroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())
    private val apiKey: String = "Mock api key"

    private fun onAllDataLoaded(loaded: Boolean) {
    }

    @Mock
    private lateinit var mMockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var endpoint: Endpoint

    private lateinit var mSharedPref: SharedPref

    @Before
    fun setup() {
        mSharedPref = createMockSharedPref()
    }

    private fun createMockSharedPref() : SharedPref {
        /*given(mMockSharedPreferences.getString(eq(SharedPref.LAST_PAGE_COUNT), anyString()))
            .willReturn("0")
        given(mMockSharedPreferences.getString(eq(SharedPref.TOTAL_ITEM_COUNT), anyString()))
            .willReturn("102")*/
        return SharedPref(mMockSharedPreferences)
    }

    @Test
    fun catDataLoaderTest_ApplyFilterForDuplicate() {
        val loader = CatDataLoader(
            null,
            mCoroutineScope,
            mSharedPref,
            apiKey,
            endpoint,
            this::onAllDataLoaded
        )
        val list = listOf(createCat(1), createCat(2),
            Cat("id3", "url3", null, listOf(createBreed(1))))
        val filteredList = loader.applyFilter(list)
        assertThat("List count after removing duplicate breed should be 2", filteredList.size, equalTo(2))
    }

    @Test
    fun catDataLoaderTest_ApplyFilterForEmpty() {
        val loader = CatDataLoader(
            null,
            mCoroutineScope,
            mSharedPref,
            apiKey,
            endpoint,
            this::onAllDataLoaded
        )
        val list = listOf(createCat(1), createCat(2), createCat(3),
            Cat("id4", "url4", null, null))
        val filteredList = loader.applyFilter(list)
        assertThat("List count after removing empty breed should be 3", filteredList.size, equalTo(3))
    }

    private fun createCat(count: Int): Cat {
        return Cat("id$count", "url$count", null, listOf(createBreed(count)))
    }

    private fun createBreed(count: Int): Breed {
        return Breed("breedId$count", "name$count", "temperament$count",
            "origin$count", "description$count", "lifespan$count")
    }
}