package com.example.vegancompanion

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Recipes : Fragment() {

    private var auth: FirebaseAuth = Firebase.auth
    private val args: RecipesArgs by navArgs()
    private var database = Firebase.database


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
        val rateButton: Button = view.findViewById(R.id.rate_button)
        val rateTextField: TextInputEditText = view.findViewById(R.id.rate_edit_text)
        var newRating:Float = 0F
        var totalRates = 0
        val dbRatingsUser = FirebaseDatabase.getInstance().getReference("ratings").child(FirebaseAuth.getInstance().currentUser!!.uid)
        val dbRatings = FirebaseDatabase.getInstance().getReference("ratings")
        val dbRecipes = FirebaseDatabase.getInstance().getReference("Recipe").child(args.id!!)

        name.text = args.name
        instructions.text = args.instructions
        ingredients.text = args.ingredients

        Glide.with(requireActivity()).applyDefaultRequestOptions(requestOptions).load(args.image).into(imageView)
        setHasOptionsMenu(true)

        rateButton.setOnClickListener {
            if(rateTextField.text.toString().toIntOrNull() == null) {
                rateTextField.error = "Please enter rating"
                rateTextField.requestFocus()}

            else if (rateTextField.text.toString().toInt() > 5 || rateTextField.text.toString().toInt() < 1) {
                rateTextField.error = "Please enter value between 1-5"
                rateTextField.requestFocus()
            }
            else {
                val rating = rateTextField.text.toString().toInt()

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

}
