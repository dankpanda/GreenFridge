package com.example.vegancompanion

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vegancompanion.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
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

        name.text = args.name
        instructions.text = args.instructions
        ingredients.text = args.ingredients

        Glide.with(requireActivity()).applyDefaultRequestOptions(requestOptions).load(args.image).into(imageView)
        setHasOptionsMenu(true)
        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_menu, menu)
        val delButton: MenuItem = menu.findItem(R.id.action_remove_bookmark)
        val addButton: MenuItem = menu.findItem(R.id.action_add_bookmark)
        if(args.bookmarked){
            delButton.isVisible = true
            addButton.isVisible = false
        } else {
            delButton.isVisible = false
            addButton.isVisible = true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when(item.itemId){
                android.R.id.home -> {val action = RecipesDirections.recipesToHome()
                    findNavController().navigate(action)}

                else -> addBookmark()
            }
        return true
    }

    private fun addBookmark() {
        val dbBookmarks = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        if(args.bookmarked) {
            Toast.makeText(activity, "Bookmark removed", Toast.LENGTH_SHORT).show()
            dbBookmarks.child(args.id!!).removeValue()
            findNavController().navigate(RecipesDirections.recipesToHome())
        }

        else{
            Toast.makeText(activity, "Bookmark added", Toast.LENGTH_SHORT).show()
            val recipe = Recipe(args.name,args.ingredients,args.instructions,args.image,args.id)
            dbBookmarks.child(args.id!!).setValue(recipe)
            findNavController().navigate(RecipesDirections.recipesToBookmark())
        }
    }


}
