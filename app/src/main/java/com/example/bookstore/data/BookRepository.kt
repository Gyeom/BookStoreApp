package com.example.bookstore.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookstore.api.BookService
import com.example.bookstore.db.BookDatabase
import com.example.bookstore.model.Book
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val service: BookService,
    private val database: BookDatabase
) {

    fun getSearchResultStream(query: String): Flow<PagingData<Book>> {
        val pagingSourceFactory = { database.booksDao().selectBooks() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = BookRemoteMediator(
                query,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun getSearchDetailsResult(id: String): List<Book> {
        return service.searchBook(id).items
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}
