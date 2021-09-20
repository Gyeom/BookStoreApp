package com.example.bookstore.api

import com.example.bookstore.model.Book
import com.google.gson.annotations.SerializedName

data class BookSearchResponse(
    @SerializedName("total") val total: Int = 0,
    @SerializedName("books") val items: List<Book> = emptyList(),
)
