package com.example.bookstore.ui.viewmodel

import androidx.lifecycle.*
import com.example.bookstore.data.BookRepository
import com.example.bookstore.model.BookSearchResult
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.Exception

class BookDetailsViewModel(
    private val repository: BookRepository
) : ViewModel() {
    private var _bookDetailsLiveData =
        MutableLiveData<BookSearchResult>(BookSearchResult.UnInitialized)
    val bookDetailsLiveData: LiveData<BookSearchResult> = _bookDetailsLiveData

    fun searchBook(id: String) = viewModelScope.launch(IO) {
        _bookDetailsLiveData.postValue(BookSearchResult.Loading)
        try {
            _bookDetailsLiveData.postValue(
                BookSearchResult.Success(
                    repository.getSearchDetailsResult(
                        id
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            _bookDetailsLiveData.postValue(BookSearchResult.Error(e))
        }
    }
}
