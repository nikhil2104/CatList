package com.intuit.catlist.persist

import android.content.SharedPreferences
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SharedPrefTest {
    companion object {
        private const val MOCK_TOTAL_ITEM_COUNT = 102;
        private const val MOCK_LAST_PAGE_COUNT = 0;
    }
    private lateinit var mSharedPref: SharedPref

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        mSharedPref = createMockSharedPref()
    }

    private fun createMockSharedPref(): SharedPref {
        given(sharedPreferences.getInt(eq(SharedPref.TOTAL_ITEM_COUNT), anyInt())).willReturn(MOCK_TOTAL_ITEM_COUNT)
        given(sharedPreferences.getInt(eq(SharedPref.LAST_PAGE_COUNT), anyInt())).willReturn(MOCK_LAST_PAGE_COUNT)
        return SharedPref(sharedPreferences)
    }

    @Test
    fun sharedPrefTest_GetTotalItemCount() {
        assertEquals(mSharedPref.getTotalItemCount(), MOCK_TOTAL_ITEM_COUNT)
    }

    @Test
    fun sharedPrefTest_GetLastPageCount() {
        assertEquals(mSharedPref.getLastRequestedPage(), MOCK_LAST_PAGE_COUNT)
    }
}