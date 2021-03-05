package com.example.vegancompanion

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vegancompanion.models.Comment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Recipes : Fragment() {

    private var auth: FirebaseAuth = Firebase.auth
    private val args: RecipesArgs by navArgs()
    private var database = Firebase.database
    private lateinit var recyclerView: RecyclerView
    private var commentAdapter: CommentAdapter = CommentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipes, container, false)
        val requestOptions = RequestOptions().placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background)
        val imageView: ImageView = view.findViewById(R.id.frg_recipe_image)
        val name: TextView = view.findViewById(R.id.frg_recipe_name_title)
        val instructions: TextView = view.findViewById(R.id.frg_recipe_instructions)
        val ingredients: TextView = view.findViewById(R.id.frg_recipe_ingredients)
        val rateBar: RatingBar = view.findViewById(R.id.ratingBar)
        val dbRatingsUser = FirebaseDatabase.getInstance().getReference("ratings").child(FirebaseAuth.getInstance().currentUser!!.uid)
        val dbRatings = FirebaseDatabase.getInstance().getReference("ratings")
        val dbRecipes = FirebaseDatabase.getInstance().getReference("Recipe").child(args.id!!)
        recyclerView = view.findViewById(R.id.comments_recycler_view)
        var newRating:Float = 0F
        var totalRates = 0
        val commentField: TextInputEditText = view.findViewById(R.id.submit_comment_text_field)
        val submitCommentButton: Button = view.findViewById(R.id.submit_comment_button)

        fetchComments()
        submitCommentButton.setOnClickListener{
            submitComment(commentField)
        }

        name.text = args.name
        instructions.text = args.instructions
        ingredients.text = args.ingredients

        Glide.with(requireActivity()).applyDefaultRequestOptions(requestOptions).load(args.image).into(imageView)
        setHasOptionsMenu(true)

        rateBar.onRatingBarChangeListener = object: RatingBar.OnRatingBarChangeListener{
            val rating = rateBar.rating
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                dbRatingsUser.child(args.id!!).setValue(rating.toString())
                dbRatings.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(i in snapshot.children){
                            if(i.child(args.id!!).exists()){
                                totalRates += 1
                                newRating += i.child(args.id!!).value.toString().toFloat()
                            }
                        }
                        newRating /= totalRates
                        dbRecipes.child("Rating").setValue(newRating.toString())
                        Toast.makeText(activity,"Rating submitted",Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(activity,"Failed to submit rating",Toast.LENGTH_SHORT).show()
                    }

                })

            }
        }
        return view
    }

    private fun submitComment(commentField: TextInputEditText) {
        val commentBody = commentField.text.toString()
        val dbComments = FirebaseDatabase.getInstance().getReference("comments").child(args.id!!)
        if(commentBody.length == 0){
            commentField.error = "Please insert comment"
            commentField.requestFocus()
        }
        else if(commentBody.length > 1000){
            commentField.error = "Comment cannot exceed 1000 characters"
            commentField.requestFocus()
        } else{
            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            val time = formatter.format(java.util.Calendar.getInstance().time).toString()
            val newComment = Comment()
            newComment.Body = commentBody
            newComment.Time = time
            newComment.User = FirebaseAuth.getInstance().currentUser!!.email.toString()
            val id = dbComments.push().key
            newComment.Id = id
            newComment.Recipe = args.id!!
            dbComments.child(id!!).setValue(newComment)
            Toast.makeText(activity,"Comment submitted",Toast.LENGTH_SHORT).show()
            commentField.setText("")
        }
    }

    private fun fetchComments(){
        val dbComments = FirebaseDatabase.getInstance().getReference("comments").child(args.id!!)
        val dataSource: MutableList<Comment> = mutableListOf()
        dbComments.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataSource.clear()
                if(snapshot.exists()){
                    for(i in snapshot.children){
                        val current = i.getValue(Comment::class.java)
                        if (current != null) {
                            dataSource.add(current)
                        }
                    }
                    commentAdapter.reloadAdapter(dataSource)
                    initializeRecyclerView(recyclerView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Failed to fetch data",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView){
        recyclerView.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = commentAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_menu, menu)
        val delButton: MenuItem = menu.findItem(R.id.action_remove_bookmark)
        val addButton: MenuItem = menu.findItem(R.id.action_add_bookmark)
        val dbBookmarks = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        if(args.bookmarked){
            delButton.isVisible = true
            addButton.isVisible = false
        } else {
            delButton.isVisible = false
            addButton.isVisible = true
        }

        delButton.setOnMenuItemClickListener {
            Toast.makeText(activity, "Bookmark removed", Toast.LENGTH_SHORT).show()
            dbBookmarks.child(args.id!!).removeValue()
            delButton.isVisible = false
            addButton.isVisible = true
            true
        }

        addButton.setOnMenuItemClickListener {
            Toast.makeText(activity, "Bookmark added", Toast.LENGTH_SHORT).show()
            dbBookmarks.child(args.id!!).child("id").setValue(args.id)
            addButton.isVisible = false
            delButton.isVisible = true
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        activity?.onBackPressed()
        return true
    }

    fun onItemClick(comment: Comment) {

    }

}
