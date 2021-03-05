package com.example.vegancompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.vegancompanion.models.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class CommentAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Comment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.comments_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CommentAdapter.ViewHolder -> {holder.checkDelete(items[position])
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    fun reloadAdapter(data: List<Comment>): CommentAdapter {
        this.items = data
        return this
    }

    class ViewHolder (
            itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        val user: TextView = itemView.findViewById(R.id.comment_user_email)
        val body: TextView = itemView.findViewById(R.id.comment_body)
        val time: TextView = itemView.findViewById(R.id.comment_time)
        val deleteButton: ImageView = itemView.findViewById(R.id.comment_delete)

        fun checkDelete(comment: Comment){
            if(FirebaseAuth.getInstance().currentUser!!.email.toString() == comment.User){
                deleteButton.setOnClickListener{
                    FirebaseDatabase.getInstance().getReference("comments").child(comment.Recipe!!).child(comment.Id!!).removeValue()
                }
            } else deleteButton.isVisible = false

        }

        fun bind(comments: Comment) {
            user.text = comments.User
            body.text = comments.Body
            time.text = comments.Time
        }

    }
}