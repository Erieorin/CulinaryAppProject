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
import com.example.culinaryappproject.ui.home.CategoryAdapter
import com.example.culinaryappproject.ui.home.RecipeViewModel
import com.example.culinaryappproject.ui.home.RecipeAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.example.culinaryappproject.ui.home.MainActivity
import com.example.culinaryappproject.ui.register.RegisterActivity


class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView // отображает список рецептов в виде сетки (2 колонки)
    private val recipeViewModel: RecipeViewModel by viewModels() // хранит и управляет данными о рецептах (получает их из API)
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var emptyStateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // делает контент полноэкранным (учитывает системные панели — статус-бар и навигацию)
        setContentView(R.layout.activity_search)

        // инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)

        emptyStateText = findViewById(R.id.emptyStateText)

        // Настройка RecyclerView для категорий
        categoriesRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // Настройка RecyclerView для рецептов
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Скрываем рецепты при старте
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE

        // Наблюдение за флагом отображения рецептов
        recipeViewModel.showRecipes.observe(this) { show ->
            if (show) {
                recyclerView.visibility = View.VISIBLE
                emptyStateText.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                emptyStateText.visibility = View.VISIBLE
            }
        }

        // Наблюдение за категориями
        recipeViewModel.categories.observe(this) { categories ->
            val adapter = CategoryAdapter(categories) { category ->
                recipeViewModel.fetchRecipesByCategory(category)
            }
            categoriesRecyclerView.adapter = adapter
        }

        // наблюдение за данными: когда recipes в ViewModel обновляются, RecyclerView автоматически получает новые данные
        recipeViewModel.recipes.observe(this) { meals ->
            val adapter = RecipeAdapter(this@SearchActivity, meals) // адаптер для преобразования данных рецептов в элементы RecyclerView
            recyclerView.layoutManager = GridLayoutManager(this@SearchActivity, 2) // GridLayoutManager размещает элементы в сетке
            recyclerView.adapter = adapter
        }

        recipeViewModel.fetchCategories()
        recipeViewModel.resetRecipes()

        // поисковое окно
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                recipeViewModel.searchRecipes(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                recipeViewModel.searchRecipes(newText)
                return false
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
}