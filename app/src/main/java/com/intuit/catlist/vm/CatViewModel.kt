package com.intuit.catlist.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.intuit.catlist.model.Cat
import com.intuit.catlist.utils.Constants
import com.intuit.catlist.persist.CatDatabase
import com.intuit.catlist.persist.SharedPref
import com.intuit.catlist.network.Endpoint
import com.intuit.catlist.paging.CatBoundaryCallback
import com.intuit.catlist.paging.InMemoryDataSourceFactory
import com.intuit.catlist.paging.InMemoryPageKeyedDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CatViewModel(apiKey: String, private val database: CatDatabase, endpoint: Endpoint,
                   private val sharedPref: SharedPref): ViewModel() {
    val dataLoadCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val cats : LiveData<PagedList<Cat>> = if (Constants.Configuration.LOAD_DATA_INMEMORY) {
        InMemoryDataSourceFactory(apiKey, endpoint, sharedPref,
            viewModelScope) { dataLoadCompleted.postValue(it) }.toLiveData(pageSize = Constants.ITEM_PER_PAGE)
    } else {
        database.getCatDao().getCatFactory().toLiveData(pageSize = Constants.ITEM_PER_PAGE,
            boundaryCallback = CatBoundaryCallback(apiKey, endpoint, database, sharedPref,
                viewModelScope) { dataLoadCompleted.value = it })
    }

    override fun onCleared() {
        if (!Constants.Configuration.LOAD_DATA_INMEMORY && Constants.Configuration.CLEAR_ON_EXIT) {
            GlobalScope.launch {
                database.getCatDao().deleteAllCats()
                sharedPref.clear()
            }
        }
        super.onCleared()
    }
}