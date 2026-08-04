package com.example.ngdungocsach.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ngdungocsach.R
import com.example.ngdungocsach.adapter.ReviewAdapter
import com.example.ngdungocsach.database.FirebaseHelper
import com.example.ngdungocsach.model.Book
import com.example.ngdungocsach.model.Review
import com.example.ngdungocsach.ui.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class AdminBookReviewsActivity : BaseActivity() {

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var rvBookReviews: RecyclerView
    private var allBooks: List<Book> = emptyList()
    private var allReviews: List<Review> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_book_reviews)

        firebaseHelper = FirebaseHelper()
        rvBookReviews = findViewById(R.id.rvBookReviews)
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }

        rvBookReviews.layoutManager = LinearLayoutManager(this)

        loadData()
    }

    private fun loadData() {
        firebaseHelper.getAllBooks { books ->
            allBooks = books
            firebaseHelper.getAllReviews { reviews ->
                allReviews = reviews
                updateUI()
            }
        }
    }

    private fun updateUI() {
        val bookReviewMap = allReviews.groupBy { it.bookId }
        val booksWithReviews = allBooks.filter { bookReviewMap.containsKey(it.id) }
            .sortedByDescending { bookReviewMap[it.id]?.size ?: 0 }

        rvBookReviews.adapter = BookReviewSummaryAdapter(booksWithReviews, bookReviewMap) { book ->
            showReviewListDialog(book, bookReviewMap[book.id] ?: emptyList())
        }
    }

    private fun showReviewListDialog(book: Book, reviews: List<Review>) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_admin_reviews, null)
        dialog.setContentView(view)

        view.findViewById<TextView>(R.id.tvDialogTitle).text = "Đánh giá: ${book.title}"
        val rvReviews = view.findViewById<RecyclerView>(R.id.rvReviews)
        rvReviews.layoutManager = LinearLayoutManager(this)
        
        val adapter = ReviewAdapter(reviews) { review ->
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa bình luận")
                .setMessage("Bạn có chắc chắn muốn xóa bình luận này vì vi phạm tiêu chuẩn cộng đồng?")
                .setPositiveButton("Xóa") { _, _ ->
                    firebaseHelper.deleteReview(review.id) { success ->
                        if (success) {
                            loadData() // Refresh data
                            dialog.dismiss()
                        }
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
        rvReviews.adapter = adapter

        view.findViewById<MaterialButton>(R.id.btnClose).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    class BookReviewSummaryAdapter(
        private val books: List<Book>,
        private val reviewMap: Map<String, List<Review>>,
        private val onClick: (Book) -> Unit
    ) : RecyclerView.Adapter<BookReviewSummaryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgBook: ImageView = view.findViewById(R.id.imgBook)
            val tvBookTitle: TextView = view.findViewById(R.id.tvBookTitle)
            val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
            val tvReviewCount: TextView = view.findViewById(R.id.tvReviewCount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_admin_book_review_summary, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val book = books[position]
            val reviews = reviewMap[book.id] ?: emptyList()
            val avgRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average().toFloat() else 0f

            holder.tvBookTitle.text = book.title
            holder.ratingBar.rating = avgRating
            holder.tvReviewCount.text = "${reviews.size} đánh giá"

            Glide.with(holder.itemView.context)
                .load(book.image)
                .placeholder(R.drawable.white)
                .into(holder.imgBook)

            holder.itemView.setOnClickListener { onClick(book) }
        }

        override fun getItemCount() = books.size
    }
}
