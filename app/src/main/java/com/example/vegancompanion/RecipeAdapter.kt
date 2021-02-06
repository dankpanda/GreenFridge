package com.example.vegancompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vegancompanion.models.Recipe

class RecipeAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Recipe> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.custom_grid_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun reloadAdapter(data: List<Recipe>): RecipeAdapter{
        this.items = data
        return this
    }

    inner class ViewHolder (
            itemView: View
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val recipe_image: ImageView = itemView.findViewById(R.id.recipe_image)
        val recipe_name: TextView = itemView.findViewById(R.id.recipe_name)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION)
            {listener.onItemClick(items[position])}
        }

        fun bind(recipes: Recipe){
            recipe_name.text=recipes.Name

            val requestOptions = RequestOptions().placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptions).load(recipes.Image).into(recipe_image)
        }


    }

    interface OnItemClickListener{
        fun onItemClick(item: Recipe)
    }


}