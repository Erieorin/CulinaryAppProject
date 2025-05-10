// главный экран приложения для отображения списка рецептов в виде сетки

package com.example.culinaryappproject.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView // отображает список рецептов в виде сетки (2 колонки)
    private val recipeViewModel: RecipeViewModel by viewModels() // хранит и управляет данными о рецептах (получает их из API)
    private lateinit var categoriesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // делает контент полноэкранным (учитывает системные панели — статус-бар и навигацию)
        setContentView(R.layout.activity_main)

        // инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)

        // Настройка RecyclerView для категорий
        categoriesRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // Настройка RecyclerView для рецептов
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Наблюдение за категориями
        recipeViewModel.categories.observe(this) { categories ->
            val adapter = CategoryAdapter(categories) { category ->
                recipeViewModel.fetchRecipesByCategory(category)
            }
            categoriesRecyclerView.adapter = adapter
        }

        // наблюдение за данными: когда recipes в ViewModel обновляются, RecyclerView автоматически получает новые данные
        recipeViewModel.recipes.observe(this) { meals ->
            val adapter = RecipeAdapter(this@MainActivity, meals) // адаптер для преобразования данных рецептов в элементы RecyclerView
            recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2) // GridLayoutManager размещает элементы в сетке
            recyclerView.adapter = adapter
        }

        recipeViewModel.fetchCategories()
        recipeViewModel.fetchRecipes() // запускает загрузку рецептов из API

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
    }
}

/*
Activity запускается → вызывает fetchRecipes() во ViewModel.
ViewModel обращается к API
Полученные данные сохраняются в LiveData/StateFlow (поле recipes)
RecyclerView обновляется через Observer (адаптер получает новые meals)
*/