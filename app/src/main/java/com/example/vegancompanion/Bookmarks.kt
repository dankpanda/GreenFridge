package com.example.vegancompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class Bookmarks : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        val textButton: TextView = view.findViewById(R.id.bookmarksTextView)
        textButton.setOnClickListener{ Navigation.findNavController(view).navigate(R.id.action_fragmentBookmarks_to_bookmarkedRecipe)}

        return view
    }

}