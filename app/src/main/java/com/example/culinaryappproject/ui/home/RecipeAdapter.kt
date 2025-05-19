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
/*
class RecipeAdapter(
    private val context: Context,
    private val meals: List<Meal>
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {
    // внутренний класс, который хранит ссылки на элементы UI
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = itemView.findViewById(R.id.mealImage)
        val textView: TextView = itemView.findViewById(R.id.mealName)
    }

    // создает новый ViewHolder и надувает (создает) макет (list_item.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ViewHolder(view)
    }

    // заполняет элементы данными из meals и обрабатывает клики
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = meals[position]

        // загрузка изображения с помощью Glide
        Glide.with(context).load(item.strMealThumb).into(holder.imageView)
        holder.textView.text = item.strMeal // установка названия рецепта

        // обработка клика, при клике на элемент открывается RecipeDetailActivity с передачей idMeal
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("MEAL_ID", item.idMeal)
            context.startActivity(intent)
        }
    }

    // возвращает общее количество элементов
    override fun getItemCount(): Int = meals.size
}
*/
class RecipeAdapter(
    private val context: Context,
    private val recipes: List<Recipe>
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.mealImage)
        val textView: TextView = view.findViewById(R.id.mealName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recipes[position]

        Glide.with(context).load(item.photoUrl).into(holder.imageView)
        holder.textView.text = item.title

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", item.id) // ключ сменился с "MEAL_ID" на "RECIPE_ID"
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recipes.size
}

/* В контексте Android-разработки слово «надувает» (от англ. inflate) — это жаргонный термин,
 который означает процесс создания объекта View из XML-макета */