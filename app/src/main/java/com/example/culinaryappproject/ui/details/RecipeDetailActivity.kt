/* экран деталей рецепта (RecipeDetailActivity), который отображает подробную информацию о блюде */

package com.example.culinaryappproject.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import androidx.core.content.res.ResourcesCompat
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.models.Recipe
import com.example.culinaryappproject.models.Review



class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeInstructions: TextView
    private lateinit var recipeIngredients: TextView

    private lateinit var instructionsContainer: LinearLayout
    private lateinit var reviewsContainer: LinearLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe_detail)

        recipeImage = findViewById(R.id.recipeImage)
        recipeName = findViewById(R.id.recipeName)
        recipeIngredients = findViewById(R.id.recipeIngredients)
        instructionsContainer = findViewById(R.id.instructionsContainer)
        reviewsContainer = findViewById(R.id.reviewsContainer)



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

            // теперь вызываем getReviews после получения recipe
            FirestoreRepository.getReviewsWithUserNames(recipe.id) { reviewsWithUsers ->
                runOnUiThread {
                    bindReviews(reviewsWithUsers)
                }
            }
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

        val toggleButton = findViewById<TextView>(R.id.toggleStepsButton)
        val instructionsContainer = findViewById<View>(R.id.instructionsContainer)

        toggleButton.setOnClickListener {
            val isVisible = instructionsContainer.visibility == View.VISIBLE

            if (isVisible) {
                instructionsContainer.visibility = View.GONE
                toggleButton.text = "Показать этапы"
                toggleButton.paintFlags = toggleButton.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
            } else {
                instructionsContainer.visibility = View.VISIBLE
                toggleButton.text = "Скрыть этапы"
                toggleButton.paintFlags = toggleButton.paintFlags and android.graphics.Paint.UNDERLINE_TEXT_FLAG.inv()
            }
        }

        val favoriteIcon = findViewById<ImageView>(R.id.favoriteIcon)
        var isFavorite = false
        val userId = "abc123"

        FirestoreRepository.getFavoriteRecipes(userId) { favorites ->
            isFavorite = favorites.contains(recipeId)
            favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled_white
                else R.drawable.ic_favorite_border_white
            )
        }

        favoriteIcon.setOnClickListener {
            isFavorite = !isFavorite

            favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled_white
                else R.drawable.ic_favorite_border_white
            )

            if (isFavorite) {
                FirestoreRepository.addToFavorites(userId, recipeId) { success ->
                    if (success) {
                        Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                FirestoreRepository.removeFromFavorites(userId, recipeId) { success ->
                    if (success) {
                        Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        val backButton = findViewById<TextView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun bindReviews(reviewsWithUsers: List<Pair<String, Review>>) {
        reviewsContainer.removeAllViews()

        val titleTextView = TextView(this).apply {
            text = "Отзывы"
            textSize = 18f
            setTextColor(Color.BLACK)
            typeface = ResourcesCompat.getFont(this@RecipeDetailActivity, R.font.bookerly)
            setTypeface(typeface, android.graphics.Typeface.BOLD) // применяем жирный стиль после загрузки
            setPadding(0, 0, 0, 16)
        }

        reviewsContainer.addView(titleTextView)

        if (reviewsWithUsers.isEmpty()) {
            val noReviewsText = TextView(this).apply {
                text = "Отзывов пока нет"
                textSize = 16f
                setTextColor(Color.GRAY)
            }
            reviewsContainer.addView(noReviewsText)
            return
        }

        for ((userName, review) in reviewsWithUsers) {
            // Горизонтальный layout для имени и оценки
            val headerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val userNameTextView = TextView(this).apply {
                text = userName
                textSize = 16f
                typeface = ResourcesCompat.getFont(this@RecipeDetailActivity, R.font.opensans)
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val ratingTextView = TextView(this).apply {
                text = "Оценка: ${review.rating}/5"
                textSize = 16f
                typeface = ResourcesCompat.getFont(this@RecipeDetailActivity, R.font.opensans)
                textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            headerLayout.addView(userNameTextView)
            headerLayout.addView(ratingTextView)

            val topPaddingPx = (8 * resources.displayMetrics.density).toInt()

            val commentTextView = TextView(this).apply {
                text = "${review.text}"
                textSize = 16f
                setPadding(0, topPaddingPx, 0, 16) // ← верхний отступ добавлен здесь
                typeface = ResourcesCompat.getFont(this@RecipeDetailActivity, R.font.opensans)
            }

            reviewsContainer.addView(headerLayout)
            reviewsContainer.addView(commentTextView)

            val divider = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (1 * resources.displayMetrics.density).toInt() // 1dp толщина
                ).apply {
                    topMargin = (8 * resources.displayMetrics.density).toInt()
                }
                setBackgroundColor(Color.GRAY)
            }
            reviewsContainer.addView(divider)

            val spacer = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (8 * resources.displayMetrics.density).toInt() // 8dp
                )
            }
            reviewsContainer.addView(spacer)


        }
    }






    private fun bindRecipeData(recipe: Recipe) {
        recipeName.text = recipe.title
        recipeIngredients.text = recipe.ingredients.joinToString("\n")

        instructionsContainer.removeAllViews()

        val inflater = layoutInflater

        for (step in recipe.steps) {
            // Заголовок этапа
            val titleView = TextView(this).apply {
                text = step.title
                setTextColor(resources.getColor(R.color.ginger))
                textSize = 18f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, 16, 0, 0)
                typeface = ResourcesCompat.getFont(this@RecipeDetailActivity, R.font.bookerly)
            }
            instructionsContainer.addView(titleView)

            // Горизонтальный LinearLayout с иконкой и временем
            val timeLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 4, 0, 4)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }


            val sizeInPx = (25 * resources.displayMetrics.density).toInt()
            val marginInPx = (8 * resources.displayMetrics.density).toInt()

            val timeIcon = ImageView(this).apply {
                setImageResource(R.drawable.ic_time)
                layoutParams = LinearLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                    rightMargin = marginInPx
                }
            }



            // Текст с временем
            val timeTextView = TextView(this).apply {
                text = "${step.duration} мин"
                textSize = 16f
                typeface = ResourcesCompat.getFont(this@RecipeDetailActivity, R.font.bookerly)
                setTextColor(Color.BLACK)
            }

            timeLayout.addView(timeIcon)
            timeLayout.addView(timeTextView)

            instructionsContainer.addView(timeLayout)

            // Описание этапа
            val descView = TextView(this).apply {
                text = step.description
                textSize = 16f
                setPadding(0, 0, 0, 8)
                typeface = ResourcesCompat.getFont(this@RecipeDetailActivity, R.font.bookerly)
            }
            instructionsContainer.addView(descView)
        }

        Glide.with(this).load(recipe.photoUrl).into(recipeImage)
    }




}
