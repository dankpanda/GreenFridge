package com.example.vegancompanion

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

class BookmarkedRecipe : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bookmarked_recipe, container, false)
        setHasOptionsMenu(true)
        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bookmarked_recipe_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}