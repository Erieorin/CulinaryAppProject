package com.example.culinaryappproject.ui.search

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import androidx.appcompat.widget.SearchView
import com.example.culinaryappproject.ui.home.RecipeViewModel
import com.example.culinaryappproject.ui.home.RecipeAdapter
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.ui.home.CuisineAdapter
import com.example.culinaryappproject.ui.home.TagAdapter
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

class SearchFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val recipeViewModel: RecipeViewModel by viewModels()

    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var cuisinesRecyclerView: RecyclerView
    private lateinit var emptyStateText: TextView

    private var selectedTag: String? = null
    private var selectedCuisine: String? = null

    private lateinit var adapter: RecipeAdapter
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        tagsRecyclerView = view.findViewById(R.id.tagsRecyclerView)
        cuisinesRecyclerView = view.findViewById(R.id.cuisinesRecyclerView)
        emptyStateText = view.findViewById(R.id.emptyStateText)

        tagsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        cuisinesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE

        val user = FirebaseAuth.getInstance().currentUser
        currentUserId = user?.uid

        adapter = RecipeAdapter(requireContext(), emptyList(), currentUserId.toString())
        recyclerView.adapter = adapter


        setupObservers()
        setupSearchView(view)
        fetchInitialData()
    }

    private fun setupObservers() {
        // Показывать/скрывать рецепты
        recipeViewModel.showRecipes.observe(viewLifecycleOwner) { show ->
            recyclerView.visibility = if (show) View.VISIBLE else View.GONE
            emptyStateText.visibility = if (show) View.GONE else View.VISIBLE
        }

        // Тэги
        recipeViewModel.tags.observe(viewLifecycleOwner) { tags ->
            val tagAdapter = TagAdapter(tags) { tag ->
                selectedTag = if (tag.isBlank()) null else tag
                updateFiltersAndRefresh()
            }
            tagsRecyclerView.adapter = tagAdapter
        }

        // Кухни
        recipeViewModel.cuisines.observe(viewLifecycleOwner) { cuisines ->
            val cuisineAdapter = CuisineAdapter(cuisines) { cuisine ->
                selectedCuisine = if (cuisine.isBlank()) null else cuisine
                updateFiltersAndRefresh()
            }
            cuisinesRecyclerView.adapter = cuisineAdapter
        }

        // Рецепты
        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateRecipes(recipes)
            recipes.forEach { recipe ->
                if (currentUserId != null) {
                    FirestoreRepository.checkIfFavorite(currentUserId!!, recipe.id) { isFavorite ->
                        recipe.isFavorite = isFavorite
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false.also {
                recipeViewModel.searchRecipes(query)
            }

            override fun onQueryTextChange(newText: String) = false.also {
                recipeViewModel.searchRecipes(newText)
            }
        })

        // Стилизация SearchView
        val searchEditText = searchView.findViewById<android.widget.EditText>(
            androidx.appcompat.R.id.search_src_text
        )
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.GRAY)
        searchEditText.setBackgroundColor(Color.WHITE)
        searchEditText.hint = "Поиск"
        searchEditText.textSize = 16f

        val searchPlate = searchView.findViewById<View>(
            androidx.appcompat.R.id.search_plate
        )
        searchPlate?.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun fetchInitialData() {
        recipeViewModel.fetchCombinedRecipes()
        recipeViewModel.fetchTags()
        recipeViewModel.fetchCuisines()
        recipeViewModel.resetRecipes()
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