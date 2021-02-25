package com.example.vegancompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vegancompanion.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Bookmarks : Fragment(), RecipeAdapter.OnItemClickListener {

    private lateinit var auth: FirebaseAuth
    private var recipeAdapter: BookmarkedAdapter = BookmarkedAdapter(this)
    private var database = Firebase.database
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.bookmarks_recycler_view)
        fetchRecipes()
    }

    override fun onItemClick(item: Recipe) {
        val image = item.Image
        val name = item.Name
        val instructions = item.Instructions
        val ingredients = item.Ingredients
        val id = item.Id!!
        val action = BookmarksDirections.bookmarksToRecipes(name, ingredients, instructions, image,id,true)
        findNavController().navigate(action)
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView){
        recyclerView.apply{
            val gridLayoutManager = GridLayoutManager(activity,2, GridLayoutManager.VERTICAL,false)
            layoutManager = gridLayoutManager
            adapter = recipeAdapter}
    }

    private fun fetchRecipes(){
        val dbRecipe = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        val dataSource: MutableList<Recipe> = mutableListOf()
        dbRecipe.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataSource.clear()
                if(snapshot.exists()){
                    for(i in snapshot.children){
                        val current = i.getValue(Recipe::class.java)
                        if (current != null) {
                            dataSource.add(current)
                        }
                    }
                    recipeAdapter.reloadAdapter(dataSource)
                    initializeRecyclerView(recyclerView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Failed to fetch data", Toast.LENGTH_SHORT).show()
            }

        })

    }


}