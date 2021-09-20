package com.example.bookstore.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.bookstore.R
import com.example.bookstore.ui.viewholder.BooksViewHolder
import com.example.bookstore.ui.viewmodel.UiModel

class BooksAdapter : PagingDataAdapter<UiModel.BookItem, ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // if (viewType == R.layout.book_view_item)
        return BooksViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.BookItem -> R.layout.book_view_item
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.BookItem -> (holder as BooksViewHolder).bind(uiModel.book)
            }
        }
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel.BookItem>() {
            override fun areItemsTheSame(
                oldItem: UiModel.BookItem,
                newItem: UiModel.BookItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: UiModel.BookItem,
                newItem: UiModel.BookItem
            ): Boolean {
                return oldItem.book.id == newItem.book.id
            }
        }
    }
}