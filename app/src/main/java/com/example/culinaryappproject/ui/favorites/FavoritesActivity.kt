package com.example.culinaryappproject.ui.favorites

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import com.example.culinaryappproject.ui.home.RecipeAdapter
import com.example.culinaryappproject.ui.home.RecipeViewModel
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.ui.search.SearchActivity
import com.example.culinaryappproject.ui.register.RegisterActivity
import com.example.culinaryappproject.models.Recipe
import com.example.culinaryappproject.ui.home.MainActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth


class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val currentUserId = user?.uid

    private var recipesObserver: Observer<List<Recipe>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)


        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RecipeAdapter(this, emptyList(), currentUserId.toString())
        recyclerView.adapter = adapter

        loadFavoriteRecipes()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
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
                R.id.nav_favorites -> true
                R.id.navigation_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_favorites
    }

    private fun loadFavoriteRecipes() {
        if (currentUserId != null) {
            FirestoreRepository.getFavoriteRecipes(currentUserId) { favoriteRecipeIds ->
                if (favoriteRecipeIds.isNotEmpty()) {
                    recipesObserver = Observer { allRecipes ->
                        if (allRecipes.isNotEmpty()) {
                            val favoriteRecipes = allRecipes.filter { it.id in favoriteRecipeIds }
                                .map { it.copy(isFavorite = true) }

                            adapter.updateRecipes(favoriteRecipes)
                        }
                    }

                    recipesObserver?.let { observer ->
                        recipeViewModel.recipes.observe(this, observer)
                    }

                    recipeViewModel.fetchCombinedRecipes()
                } else {
                    adapter.updateRecipes(emptyList())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recipesObserver?.let { observer ->
            recipeViewModel.recipes.removeObserver(observer)
        }
    }
}

