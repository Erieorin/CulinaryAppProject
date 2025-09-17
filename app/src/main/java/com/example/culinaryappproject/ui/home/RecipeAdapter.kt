/*
RecipeAdapter - адаптер для RecyclerView, который отображает список рецептов
Связывает данные (List<Meal>) с элементами RecyclerView.
Отображает:
Изображение рецепта (strMealThumb)
Название рецепта (strMeal)
Обрабатывает клики для перехода на экран деталей (RecipeDetailActivity)
*/

package com.example.culinaryappproject.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.culinaryappproject.models.Recipe
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.culinaryappproject.R
import com.example.culinaryappproject.ui.details.RecipeDetailActivity
import com.example.culinaryappproject.models.Meal
import com.example.culinaryappproject.models.FirestoreRepository

class RecipeAdapter(
    private val context: Context,
    private var recipes: List<Recipe>,
    private val userId: String
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.mealImage)
        val textView: TextView = view.findViewById(R.id.mealName)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)

        fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recipes[position]

        Glide.with(context).load(item.photoUrl).into(holder.imageView)
        holder.textView.text = item.title
        holder.updateFavoriteIcon(item.isFavorite)

        holder.itemView.findViewById<TextView>(R.id.cookingTime).text = "${item.cookingTime} мин"
        holder.itemView.findViewById<TextView>(R.id.portionsNumber).text = "${item.servings} порции"

        val tagsText = buildString {
            if (item.cuisine.isNotBlank()) {
                append(item.cuisine)
            }
            if (item.tags.isNotEmpty()) {
                if (isNotEmpty()) append(", ")
                append(item.tags.joinToString(", "))
            }
        }
        holder.itemView.findViewById<TextView>(R.id.recipeTags).text = tagsText

        // обработчик для иконки избранного
        holder.favoriteIcon.setOnClickListener {
            val newFavoriteState = !item.isFavorite
            item.isFavorite = newFavoriteState
            holder.updateFavoriteIcon(newFavoriteState)

            if (newFavoriteState) {
                FirestoreRepository.addToFavorites(userId, item.id) { success ->
                    if (!success) {
                        item.isFavorite = false
                        notifyItemChanged(position)
                    }
                }
            } else {
                FirestoreRepository.removeFromFavorites(userId, item.id) { success ->
                    if (!success) {
                        item.isFavorite = true
                        notifyItemChanged(position)
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", item.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        this.recipes = newRecipes
        notifyDataSetChanged()
    }
}