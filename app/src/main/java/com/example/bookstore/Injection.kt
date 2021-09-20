package com.example.bookstore

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.bookstore.api.BookService
import com.example.bookstore.data.BookRepository
import com.example.bookstore.db.BookDatabase
import com.example.bookstore.ui.viewmodel.ViewModelFactory

object Injection {

    private fun provideBookRepository(context: Context): BookRepository {
        return BookRepository(BookService.create(), BookDatabase.getInstance(context))
    }

    fun provideViewModelFactory(context: Context, owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideBookRepository(context))
    }
}
