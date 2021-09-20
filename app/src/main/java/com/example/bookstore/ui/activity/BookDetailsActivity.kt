package com.example.bookstore.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookstore.Injection
import com.example.bookstore.databinding.ActivityBookDetailsBinding
import com.example.bookstore.model.BookSearchResult
import com.example.bookstore.ui.viewmodel.BookDetailsViewModel

class BookDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra(ID)
        val binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the view model
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                context = this,
                owner = this
            )
        )
            .get(BookDetailsViewModel::class.java)

        // bind the state
        binding.bindDetails(
            uiState = viewModel.bookDetailsLiveData
        ).also {
            viewModel.searchBook(id)
        }
    }

    private fun ActivityBookDetailsBinding.bindDetails(
        uiState: LiveData<BookSearchResult>
    ) {
        uiState.observe(this@BookDetailsActivity, { result ->
            if(result is BookSearchResult.Success){
                with(this){
                    result.data.getOrNull(0)?.apply {
                        bookName.text = title
                        bookDescription.text = subtitle
                        Glide.with(this@BookDetailsActivity).load(image).into(bookImage)
                    }
                }
            }
            retryButton.isVisible = result is BookSearchResult.Error
            progressBar.isVisible = result is BookSearchResult.Loading
        })
    }

    companion object {
        private const val ID = "id"
    }
}
