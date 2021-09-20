package com.example.bookstore.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val bookId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
