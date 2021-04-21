package com.intuit.catlist.persist

import android.content.SharedPreferences

class SharedPref(private val sharedPref: SharedPreferences) {
    companion object {
        internal const val LAST_PAGE_COUNT = "last_page_count"
        internal const val TOTAL_ITEM_COUNT = "total_item_count"
    }

    public fun getLastRequestedPage() : Int {
        return sharedPref.getInt(LAST_PAGE_COUNT, 0)
    }

    public fun setLastRequestedPage(count: Int) {
        sharedPref.edit().putInt(LAST_PAGE_COUNT, count).apply()
    }

    public fun getTotalItemCount() : Int {
        return sharedPref.getInt(TOTAL_ITEM_COUNT, 0)
    }

    public fun setTotalItemCount(count: Int) {
        sharedPref.edit().putInt(TOTAL_ITEM_COUNT, count).apply()
    }

    public fun clear() {
        sharedPref.edit().clear().apply()
    }
}