/* экран деталей рецепта (RecipeDetailActivity), который отображает подробную информацию о блюде */

package com.example.culinaryappproject.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.culinaryappproject.R
import com.example.culinaryappproject.models.MealDetail
import com.example.culinaryappproject.ui.home.MainActivity
import com.example.culinaryappproject.ui.search.SearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImage: ImageView // показывает изображение блюда
    private lateinit var recipeName: TextView // отображает название блюда
    private lateinit var recipeInstructions: TextView // показывает пошаговый рецепт
    private lateinit var recipeIngredients: TextView // список ингредиентов с их количествами
    private val recipeDetailViewModel: RecipeDetailViewModel by viewModels() // загружает данные рецепта из API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe_detail)

        recipeImage = findViewById(R.id.recipeImage)
        recipeName = findViewById(R.id.recipeName)
        recipeInstructions = findViewById(R.id.recipeInstructions)
        recipeIngredients = findViewById(R.id.recipeIngredients)

        val mealId = intent.getStringExtra("MEAL_ID") ?: run {
            Toast.makeText(this, "Recipe ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        recipeDetailViewModel.fetchRecipeDetails(mealId)

        recipeDetailViewModel.recipeDetails.observe(this, { meal ->
            meal?.let {
                recipeName.text = it.strMeal
                recipeInstructions.text = it.strInstructions
                recipeIngredients.text = getIngredients(it)
                Glide.with(this).load(it.strMealThumb).into(recipeImage)
            }
        })

        // нижняя навигация
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    finish()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    finish()
                    true
                }
                else -> false
            }
        }
        val transparentStates = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                Color.BLACK,
                Color.BLACK
            )
        )

        bottomNav.itemIconTintList = transparentStates
        bottomNav.itemTextColor = transparentStates
    }

    private fun getIngredients(meal: MealDetail): String {
        val ingredients = mutableListOf<String>()
        for (i in 1..20) {
            val ingredient = meal.javaClass.getDeclaredField("strIngredient$i").apply { isAccessible = true }.get(meal) as? String
            val measure = meal.javaClass.getDeclaredField("strMeasure$i").apply { isAccessible = true }.get(meal) as? String

            if (!ingredient.isNullOrEmpty()) {
                ingredients.add("$ingredient - $measure")
            }
        }
        return ingredients.joinToString("\n")
    }
}