package com.example.bookstore.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "books")
data class Book(
    @PrimaryKey @field:SerializedName("isbn13") val id: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("subtitle") val subtitle: String,
    @field:SerializedName("price") val price: String,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("url") val url: String,
)