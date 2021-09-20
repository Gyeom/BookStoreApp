package com.example.bookstore.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstore.Injection
import com.example.bookstore.databinding.ActivitySearchBooksBinding
import com.example.bookstore.ui.*
import com.example.bookstore.ui.adapter.BooksAdapter
import com.example.bookstore.ui.adapter.BooksLoadStateAdapter
import com.example.bookstore.ui.viewmodel.SearchBooksViewModel
import com.example.bookstore.ui.viewmodel.UiAction
import com.example.bookstore.ui.viewmodel.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchBooksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBooksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the view model
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                context = this,
                owner = this
            )
        )
            .get(SearchBooksViewModel::class.java)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        // bind the state
        binding.bindState(
            uiState = viewModel.state,
            uiActions = viewModel.accept
        )
    }

    private fun ActivitySearchBooksBinding.bindState(
        uiState: StateFlow<UiState>,
        uiActions: (UiAction) -> Unit
    ) {
        val bookAdapter = BooksAdapter()
        val header = BooksLoadStateAdapter { bookAdapter.retry() }
        list.adapter = bookAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = BooksLoadStateAdapter { bookAdapter.retry() }
        )
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            header = header,
            bookAdapter = bookAdapter,
            uiState = uiState,
            onScrollChanged = uiActions
        )
    }

    private fun ActivitySearchBooksBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchBook.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchBook.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchBook::setText)
        }
    }

    private fun ActivitySearchBooksBinding.updateRepoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchBook.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun ActivitySearchBooksBinding.bindList(
        header: BooksLoadStateAdapter,
        bookAdapter: BooksAdapter,
        uiState: StateFlow<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { bookAdapter.retry() }
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = bookAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        val pagingData = uiState
            .map { it.pagingData }
            .distinctUntilChanged()

        lifecycleScope.launch {
            combine(shouldScrollToTop, pagingData, ::Pair)
                // Each unique PagingData should be submitted once, take the latest from
                // shouldScrollToTop
                .distinctUntilChangedBy { it.second }
                .collectLatest { (shouldScroll, pagingData) ->
                    bookAdapter.submitData(pagingData)
                    // Scroll only after the data has been submitted to the adapter,
                    // and is a fresh search
                    if (shouldScroll) list.scrollToPosition(0)
                }
        }

        lifecycleScope.launch {
            bookAdapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && bookAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && bookAdapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                list.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && bookAdapter.itemCount == 0
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        this@SearchBooksActivity,
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
