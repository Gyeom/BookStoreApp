package com.example.bookstore.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.bookstore.api.BookService
import com.example.bookstore.db.BookDatabase
import com.example.bookstore.db.RemoteKeys
import com.example.bookstore.model.Book
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
private const val OR = '|'
private const val NOT = '-'
@OptIn(ExperimentalPagingApi::class)
class BookRemoteMediator(
    private val query: String,
    private val service: BookService,
    private val bookDatabase: BookDatabase
) : RemoteMediator<Int, Book>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Book>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val books = getBooks(query, page)
            val endOfPaginationReached = books.isEmpty()
            bookDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    bookDatabase.remoteKeysDao().clearRemoteKeys()
                    bookDatabase.booksDao().clearBooks()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = books.map {
                    RemoteKeys(bookId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                bookDatabase.remoteKeysDao().insertAll(keys)
                bookDatabase.booksDao().insertAll(books)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getBooks(
        query: String,
        page: Int
    ): List<Book> {
        if(query.contains(OR)){
            val params = query.split(OR)
            return service.searchBooks(params[0], page).items
                .plus(service.searchBooks(params[1], page).items)
        }else if(query.contains(NOT)){
            val params = query.split(NOT)
            return service.searchBooks(params[0], page).items
                .filter { !it.title.contains(params[1]) }
        }
        return service.searchBooks(query, page).items
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Book>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { book ->
                bookDatabase.remoteKeysDao().remoteKeysBookId(book.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Book>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { book ->
                bookDatabase.remoteKeysDao().remoteKeysBookId(book.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Book>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { bookId ->
                bookDatabase.remoteKeysDao().remoteKeysBookId(bookId)
            }
        }
    }
}
