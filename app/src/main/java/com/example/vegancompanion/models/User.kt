package com.example.vegancompanion.models

data class User(
    var id: String? = null,
    val bookmarks: MutableList<Recipe> = mutableListOf()
    ){
}