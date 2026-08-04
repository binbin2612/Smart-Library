package com.example.ngdungocsach.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.ngdungocsach.R
import com.example.ngdungocsach.model.Review
import java.text.SimpleDateFormat
import java.util.*

class ReviewAdapter(
    private var reviews: List<Review>,
    private val onDeleteClick: ((Review) -> Unit)? = null
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUser: TextView = view.findViewById(R.id.txtUser)
        val txtComment: TextView = view.findViewById(R.id.txtComment)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val ratingBar: RatingBar = view.findViewById(R.id.itemRating)
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val btnDelete: View = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        
        // Hiển thị "Ẩn danh" nếu tên trống
        holder.txtUser.text = if (review.displayName.isNullOrEmpty()) "Ẩn danh" else review.displayName
        
        holder.txtComment.text = review.comment
        holder.ratingBar.rating = review.rating
        
        // Mặc định avatar (có thể mở rộng để load ảnh từ URL nếu có)
        holder.imgAvatar.setImageResource(R.drawable.ic_user)
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.txtDate.text = sdf.format(Date(review.timestamp))

        if (onDeleteClick != null) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener { onDeleteClick.invoke(review) }
        } else {
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}