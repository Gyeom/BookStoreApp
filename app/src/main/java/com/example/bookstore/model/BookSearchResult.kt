package com.example.bookstore.model

import java.lang.Exception

sealed class BookSearchResult {
    object UnInitialized : BookSearchResult()
    object Loading : BookSearchResult()
    data class Success(val data: List<Book>) : BookSearchResult()
    data class Error(val error: Exception) : BookSearchResult()
}
