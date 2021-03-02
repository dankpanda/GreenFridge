package com.example.vegancompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
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

class Bookmarks : Fragment(), BookmarkedAdapter.OnItemClickListener {

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
        val emptyTextView: TextView = view.findViewById(R.id.emptyTextView)
        val dbBookmarks = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        dbBookmarks.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                emptyTextView.isVisible = !snapshot.hasChildren()
                }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve bookmarks",Toast.LENGTH_SHORT).show()
            }

        })
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
        val dbBookmarks = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        val dbRecipes = FirebaseDatabase.getInstance().getReference("Recipe")
        val dataSource: MutableList<Recipe> = mutableListOf()
        val tempDataSource: MutableList<String> = mutableListOf()
        dbBookmarks.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(i in snapshot.children){
                    tempDataSource.add(i.key.toString())
                }

                dbRecipes.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataSource.clear()
                        for(i in snapshot.children){
                            if(i.key.toString() in tempDataSource){
                                val current = i.getValue(Recipe::class.java)
                                dataSource.add(current!!)
                            }
                        }
                        recipeAdapter.reloadAdapter(dataSource)
                        initializeRecyclerView(recyclerView)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(activity,"Failed to fetch bookmarks list",Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Failed to fetch bookmarks list",Toast.LENGTH_SHORT).show()
            }

        })


    }


}