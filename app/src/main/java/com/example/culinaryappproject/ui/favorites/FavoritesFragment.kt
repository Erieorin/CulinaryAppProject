package com.example.culinaryappproject.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import com.example.culinaryappproject.ui.home.RecipeAdapter
import com.example.culinaryappproject.ui.home.RecipeViewModel
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.models.Recipe

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth


class FavoritesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter
    private var userId: String? = null
    private var recipesObserver: Observer<List<Recipe>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        userId = user?.uid

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RecipeAdapter(requireContext(), emptyList(), userId.toString())
        recyclerView.adapter = adapter

        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        if (userId != null) {
            FirestoreRepository.getFavoriteRecipes(userId!!) { favoriteRecipeIds ->
                if (favoriteRecipeIds.isNotEmpty()) {
                    recipesObserver = Observer { allRecipes ->
                        if (allRecipes.isNotEmpty()) {
                            val favoriteRecipes = allRecipes.filter { it.id in favoriteRecipeIds }
                                .map { it.copy(isFavorite = true) }

                            adapter.updateRecipes(favoriteRecipes)
                        }
                    }

                    recipesObserver?.let { observer ->
                        recipeViewModel.recipes.observe(viewLifecycleOwner, observer)
                    }

                    recipeViewModel.fetchCombinedRecipes()
                } else {
                    adapter.updateRecipes(emptyList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recipesObserver?.let { observer ->
            recipeViewModel.recipes.removeObserver(observer)
        }
    }
}

