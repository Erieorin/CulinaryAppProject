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

import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.models.Recipe


class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeInstructions: TextView
    private lateinit var recipeIngredients: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe_detail)

        recipeImage = findViewById(R.id.recipeImage)
        recipeName = findViewById(R.id.recipeName)
        recipeInstructions = findViewById(R.id.recipeInstructions)
        recipeIngredients = findViewById(R.id.recipeIngredients)

        val recipeId = intent.getStringExtra("RECIPE_ID")

        if (recipeId.isNullOrEmpty()) {
            Toast.makeText(this, "ID рецепта не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        FirestoreRepository.getRecipeById(recipeId) { recipe ->
            if (recipe == null) {
                Toast.makeText(this, "Рецепт не найден", Toast.LENGTH_SHORT).show()
                finish()
                return@getRecipeById
            }

            bindRecipeData(recipe)
        }

        // Нижняя навигация
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
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
            intArrayOf(Color.BLACK, Color.BLACK)
        )
        bottomNav.itemIconTintList = transparentStates
        bottomNav.itemTextColor = transparentStates
    }

    private fun bindRecipeData(recipe: Recipe) {
        recipeName.text = recipe.title
        recipeIngredients.text = recipe.ingredients.joinToString("\n")
        recipeInstructions.text = recipe.steps.joinToString("\n\n") {
            "${it.title}\n${it.description}\nВремя: ${it.duration} мин"
        }
        Glide.with(this).load(recipe.photoUrl).into(recipeImage)
    }
}
