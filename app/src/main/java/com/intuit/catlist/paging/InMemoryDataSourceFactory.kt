package com.intuit.catlist.paging

import androidx.paging.DataSource
import com.intuit.catlist.model.Cat
import com.intuit.catlist.network.Endpoint
import com.intuit.catlist.persist.SharedPref
import kotlinx.coroutines.CoroutineScope

class InMemoryDataSourceFactory(private val apiKey: String, private val endpoint: Endpoint,
                                private val sharedPref: SharedPref, private val coroutineScope: CoroutineScope,
                                private val onAllDataLoaded: (Boolean) -> Unit
) : DataSource.Factory<Int, Cat>() {
  override fun create(): DataSource<Int, Cat> {
    return InMemoryPageKeyedDataSource(apiKey, endpoint, sharedPref, coroutineScope, onAllDataLoaded)
  }
}