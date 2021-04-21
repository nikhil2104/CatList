package com.intuit.catlist.paging

import androidx.paging.PagedList
import com.intuit.catlist.model.Cat
import com.intuit.catlist.persist.CatDatabase
import com.intuit.catlist.persist.SharedPref
import com.intuit.catlist.network.Endpoint
import com.intuit.catlist.utils.CatDataLoader
import com.intuit.catlist.utils.Constants
import kotlinx.coroutines.*

class CatBoundaryCallback(apiKey: String, endpoint: Endpoint,
                          private val db: CatDatabase, sharedPref: SharedPref,
                          private val coroutineScope: CoroutineScope, onAllDataLoaded: (Boolean) -> Unit
) : PagedList.BoundaryCallback<Cat>() {

    private lateinit var loader: CatDataLoader

    init {
        coroutineScope.launch {
            var list: List<Cat>? = null
            if (!Constants.Configuration.CLEAR_ON_EXIT) {
                list = db.getCatDao().getCats()
            }
            loader = CatDataLoader(list, coroutineScope, sharedPref, apiKey, endpoint, onAllDataLoaded)
        }
    }

    override fun onZeroItemsLoaded() {
        loader.loadInitialData(this::onDataLoaded)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Cat) {
        loader.loadNextData(this::onDataLoaded)
    }

    private fun onDataLoaded(loadedList: List<Cat>) {
        coroutineScope.launch {
            db.getCatDao().insertCats(loadedList)
        }
    }
}