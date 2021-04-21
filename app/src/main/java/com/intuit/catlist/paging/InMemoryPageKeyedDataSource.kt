package com.intuit.catlist.paging

import androidx.paging.PageKeyedDataSource
import com.intuit.catlist.model.Cat
import com.intuit.catlist.network.Endpoint
import com.intuit.catlist.persist.SharedPref
import com.intuit.catlist.utils.CatDataLoader
import kotlinx.coroutines.CoroutineScope

class InMemoryPageKeyedDataSource(apiKey: String, endpoint: Endpoint, sharedPref: SharedPref,
                                  coroutineScope: CoroutineScope, onAllDataLoaded: (Boolean) -> Unit
) : PageKeyedDataSource<Int, Cat>() {
  private var loader: CatDataLoader = CatDataLoader(null, coroutineScope, sharedPref, apiKey, endpoint, onAllDataLoaded)
  private var count = 0

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Cat>) {
    loader.loadInitialData {
      callback.onResult(it, 0, ++count)
    }
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Cat>) {
    //Don't do anything
  }

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Cat>) {
    loader.loadNextData {
      callback.onResult(it, ++count)
    }
  }
}