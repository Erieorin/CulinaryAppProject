package com.example.culinaryappproject.ui.search

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import androidx.appcompat.widget.SearchView
import com.example.culinaryappproject.ui.home.RecipeViewModel
import com.example.culinaryappproject.ui.home.RecipeAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.example.culinaryappproject.ui.favorites.FavoritesActivity
import com.example.culinaryappproject.ui.home.CuisineAdapter
import com.example.culinaryappproject.ui.home.MainActivity
import com.example.culinaryappproject.ui.home.TagAdapter
import com.example.culinaryappproject.ui.register.RegisterActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val recipeViewModel: RecipeViewModel by viewModels()

    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var cuisinesRecyclerView: RecyclerView
    private lateinit var emptyStateText: TextView

    private var selectedTag: String? = null
    private var selectedCuisine: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        tagsRecyclerView = findViewById(R.id.tagsRecyclerView)
        cuisinesRecyclerView = findViewById(R.id.cuisinesRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)

        tagsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        cuisinesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE

        recipeViewModel.fetchCombinedRecipes()

        // Показывать/скрывать рецепты
        recipeViewModel.showRecipes.observe(this) { show ->
            recyclerView.visibility = if (show) View.VISIBLE else View.GONE
            emptyStateText.visibility = if (show) View.GONE else View.VISIBLE
        }

        // Тэги
        recipeViewModel.tags.observe(this) { tags ->
            val adapter = TagAdapter(tags) { tag ->
                selectedTag = if (tag.isBlank()) null else tag
                updateFiltersAndRefresh()
            }
            tagsRecyclerView.adapter = adapter
        }

        // Кухни
        recipeViewModel.cuisines.observe(this) { cuisines ->
            val adapter = CuisineAdapter(cuisines) { cuisine ->
                selectedCuisine = if (cuisine.isBlank()) null else cuisine
                updateFiltersAndRefresh()
            }
            cuisinesRecyclerView.adapter = adapter
        }


        // Рецепты
        recipeViewModel.recipes.observe(this) { meals ->
            val adapter = RecipeAdapter(this, meals, "user123")
            recyclerView.adapter = adapter
        }

        recipeViewModel.fetchTags()
        recipeViewModel.fetchCuisines() // новый вызов
        recipeViewModel.resetRecipes()

        // Поиск
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false.also {
                recipeViewModel.searchRecipes(query)
            }

            override fun onQueryTextChange(newText: String) = false.also {
                recipeViewModel.searchRecipes(newText)
            }
        })

        val searchEditText = searchView.findViewById<android.widget.EditText>(
            androidx.appcompat.R.id.search_src_text
        )
        searchEditText.setTextColor(android.graphics.Color.BLACK)
        searchEditText.setHintTextColor(android.graphics.Color.GRAY)
        searchEditText.setBackgroundColor(android.graphics.Color.WHITE)
        searchEditText.hint = "Поиск"
        searchEditText.textSize = 16f

        val searchPlate = searchView.findViewById<View>(
            androidx.appcompat.R.id.search_plate
        )
        searchPlate?.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        // Навигация
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
                R.id.nav_search -> true
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    finish()
                    true
                }
                R.id.navigation_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_search
    }

    private fun updateFiltersAndRefresh() {
        // Обновляем адаптеры чтобы показать выбранные фильтры
        (tagsRecyclerView.adapter as? TagAdapter)?.setSelectedTag(selectedTag)
        (cuisinesRecyclerView.adapter as? CuisineAdapter)?.setSelectedCuisine(selectedCuisine)

        // Применяем фильтры
        recipeViewModel.fetchRecipesByTagAndCuisine(selectedTag, selectedCuisine)

        // Обновляем видимость
        val hasFilters = selectedTag != null || selectedCuisine != null
        recyclerView.visibility = if (hasFilters) View.VISIBLE else View.GONE
        emptyStateText.visibility = if (hasFilters) View.GONE else View.VISIBLE
    }
}

