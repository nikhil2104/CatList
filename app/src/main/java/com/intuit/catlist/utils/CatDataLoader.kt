package com.intuit.catlist.utils

import com.intuit.catlist.model.Cat
import com.intuit.catlist.model.NetworkResponse
import com.intuit.catlist.network.Endpoint
import com.intuit.catlist.persist.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CatDataLoader(initialList: List<Cat>?, private val coroutineScope: CoroutineScope,
                    private val sharedPref: SharedPref, private val apiKey: String, private val endpoint: Endpoint,
                    private val onAllDataLoaded: (Boolean) -> Unit) {
  companion object {
    private const val PAGINATION_COUNT_HEADER = "Pagination-Count"
  }
  private val catSet = mutableSetOf<String>()

  init {
    if (!Constants.Configuration.LOAD_DATA_INMEMORY && !Constants.Configuration.CLEAR_ON_EXIT) {
      if (Constants.Configuration.REMOVE_DUPLICATES) {
        initialList?.forEach { cat ->
          cat.breed?.name?.let { it ->
            catSet.add(it)
          }
        }
      }
      if (sharedPref.getLastRequestedPage() == 0
          || sharedPref.getLastRequestedPage() * Constants.ITEM_PER_PAGE < sharedPref.getTotalItemCount()) {
        onAllDataLoaded(false)
      } else {
        onAllDataLoaded(true)
      }
    } else {
      onAllDataLoaded(false)
    }
  }

  fun loadInitialData(onDataLoaded: (List<Cat>) -> Unit) {
    coroutineScope.launch {
      val catListResponse = Endpoint.safeApiCall {
        val response = endpoint.getCatList(apiKey = apiKey, page = 1)
        val totalItemCount = response.headers().get(PAGINATION_COUNT_HEADER)?.toInt() ?: 0
        sharedPref.setTotalItemCount(totalItemCount)
        response
      }
      if (catListResponse is NetworkResponse.Success) {
        onDataLoaded(applyFilter(catListResponse.body))
        sharedPref.setLastRequestedPage(1)
      }
    }
  }

  fun loadNextData(onDataLoaded: (List<Cat>) -> Unit) {
    var sharedPrefLastRequestedPage = sharedPref.getLastRequestedPage()
    var curRequestingPage = sharedPrefLastRequestedPage + 1

    if (sharedPrefLastRequestedPage * Constants.ITEM_PER_PAGE < sharedPref.getTotalItemCount()) {
      coroutineScope.launch {
        while (sharedPrefLastRequestedPage * Constants.ITEM_PER_PAGE < sharedPref.getTotalItemCount()) {
          val catListResponse = Endpoint.safeApiCall {
            val response =
                endpoint.getCatList(apiKey = apiKey, page = curRequestingPage)
            response
          }
          if (catListResponse is NetworkResponse.Success) {
            val filteredList = applyFilter(catListResponse.body)
            if (filteredList.isNotEmpty()) {
              onDataLoaded(filteredList)
            }
            sharedPref.setLastRequestedPage(curRequestingPage/*lastRequestedPage*/)
            if (filteredList.isEmpty()) {
              sharedPrefLastRequestedPage = curRequestingPage
              curRequestingPage += 1
            } else {
              break
            }
          } else {
            break
          }
        }
        if (sharedPref.getLastRequestedPage() * Constants.ITEM_PER_PAGE >= sharedPref.getTotalItemCount()) {
          onAllDataLoaded(true)
        }
      }
    } else {
      onAllDataLoaded(true)
    }
  }

  fun applyFilter(cats: List<Cat>) : List<Cat> {
    var mutableCatList = cats.toMutableList().filter {
      it.breeds != null && it.breeds.isNotEmpty()
    }
    mutableCatList.forEach{
      it.breed = it.breeds!![0]
    }
    if (Constants.Configuration.REMOVE_DUPLICATES) {
      mutableCatList = mutableCatList.filter {
        val catBreedId = it.breed!!.id
        val accept = !catSet.contains(catBreedId)
        catSet.add(catBreedId)
        accept
      }.toMutableList()
    }
    return mutableCatList
  }
}