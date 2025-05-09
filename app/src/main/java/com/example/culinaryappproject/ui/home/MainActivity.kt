// главный экран приложения для отображения списка рецептов в виде сетки

package com.example.culinaryappproject.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView // отображает список рецептов в виде сетки (2 колонки)
    private val recipeViewModel: RecipeViewModel by viewModels() // хранит и управляет данными о рецептах (получает их из API)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // делает контент полноэкранным (учитывает системные панели — статус-бар и навигацию)
        setContentView(R.layout.activity_main)

        // инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)

        // наблюдение за данными: когда recipes в ViewModel обновляются, RecyclerView автоматически получает новые данные
        recipeViewModel.recipes.observe(this) { meals ->
            val adapter = RecipeAdapter(this@MainActivity, meals) // адаптер для преобразования данных рецептов в элементы RecyclerView
            recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2) // GridLayoutManager размещает элементы в сетке
            recyclerView.adapter = adapter
        }

        recipeViewModel.fetchRecipes() // запускает загрузку рецептов из API
    }
}

/*
Activity запускается → вызывает fetchRecipes() во ViewModel.
ViewModel обращается к API
Полученные данные сохраняются в LiveData/StateFlow (поле recipes)
RecyclerView обновляется через Observer (адаптер получает новые meals)
*/