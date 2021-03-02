package com.example.vegancompanion

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vegancompanion.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Home : Fragment() , RecipeAdapter.OnItemClickListener{

    private lateinit var auth: FirebaseAuth
    private var recipeAdapter: RecipeAdapter = RecipeAdapter(this)
    private var database = Firebase.database
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.home_recycler_view)
        fetchRecipes()
    }

    override fun onItemClick(item: Recipe) {
        val image = item.Image
        val name = item.Name
        val instructions = item.Instructions
        val ingredients = item.Ingredients
        val id = item.Id!!
        val dbBookmarks = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        dbBookmarks.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(id)){
                    val action = HomeDirections.homeToRecipes(name, ingredients, instructions, image,id,true)
                    findNavController().navigate(action)
                } else {
                    val action = HomeDirections.homeToRecipes(name, ingredients, instructions, image,id,false)
                    findNavController().navigate(action)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Failed to retrieve bookmarks",Toast.LENGTH_SHORT).show()
            }
        }
        )

    }


    private fun initializeRecyclerView(recyclerView: RecyclerView){
        recyclerView.apply{
            val gridLayoutManager = GridLayoutManager(activity,2,GridLayoutManager.VERTICAL,false)
            layoutManager = gridLayoutManager
            adapter = recipeAdapter}
    }

    private fun fetchRecipes(){
        val dbRecipe = FirebaseDatabase.getInstance().getReference("Recipe")
        val dataSource: MutableList<Recipe> = mutableListOf()
        dbRecipe.addValueEventListener(object: ValueEventListener{
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
                Toast.makeText(activity,"Failed to fetch data",Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        logOut()
        return true
    }

    private fun logOut(){
        auth= Firebase.auth
        Toast.makeText(activity,"Logged out from ${FirebaseAuth.getInstance().currentUser?.email}",Toast.LENGTH_SHORT).show()
        Firebase.auth.signOut()
        val intent = Intent(
                activity,
                LoginActivity::class.java
        )
        activity?.startActivity(intent)


    }
}