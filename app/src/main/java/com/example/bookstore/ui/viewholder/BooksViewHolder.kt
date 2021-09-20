package com.example.bookstore.ui.viewholder

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstore.R
import com.example.bookstore.model.Book
import com.example.bookstore.ui.activity.BookDetailsActivity

class BooksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.book_name)
    private val description: TextView = view.findViewById(R.id.book_description)
    private val image: ImageView = view.findViewById(R.id.book_image)
    private var book: Book? = null

    init {
        view.setOnClickListener {
            book?.id?.let { id ->
                val intent = Intent(it.context, BookDetailsActivity::class.java).apply {
                    putExtra(ID, id)
                }
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(book: Book?) {
        if (book == null) {
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
        } else {
            showBookData(book)
        }
    }

    private fun showBookData(book: Book) {
        this.book = book
        name.text = book.title
        description.text = book.subtitle
        Glide.with(this.itemView.context).load(book.image).into(this.image)
    }

    companion object {
        private const val ID = "id"
        fun create(parent: ViewGroup): BooksViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_view_item, parent, false)
            return BooksViewHolder(view)
        }
    }
}
