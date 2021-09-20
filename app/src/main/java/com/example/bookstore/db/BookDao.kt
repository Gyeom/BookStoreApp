package com.example.bookstore.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookstore.model.Book

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<Book>)

    @Query(
        "SELECT * FROM books"
    )
    fun selectBooks(): PagingSource<Int, Book>

    @Query("DELETE FROM books")
    suspend fun clearBooks()
}
