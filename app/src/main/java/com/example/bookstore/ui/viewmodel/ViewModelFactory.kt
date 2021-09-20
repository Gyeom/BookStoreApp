package com.example.bookstore.ui.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.bookstore.data.BookRepository

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: BookRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(SearchBooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchBooksViewModel(repository, handle) as T
        }else if(modelClass.isAssignableFrom(BookDetailsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return BookDetailsViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
