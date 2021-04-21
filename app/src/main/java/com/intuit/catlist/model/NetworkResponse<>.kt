package com.intuit.catlist.model

sealed class NetworkResponse<T>(body: T?, error: String?) {
    data class Success<T>(val body: T) : NetworkResponse<T>(body, null)
    data class Error<T>(val error: String) : NetworkResponse<T>(null, error)
}
