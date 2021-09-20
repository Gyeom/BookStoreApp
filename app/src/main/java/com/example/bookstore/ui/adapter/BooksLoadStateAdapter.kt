package com.example.bookstore.ui.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.bookstore.ui.viewholder.BooksLoadStateViewHolder

class BooksLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<BooksLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: BooksLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BooksLoadStateViewHolder {
        return BooksLoadStateViewHolder.create(parent, retry)
    }
}
