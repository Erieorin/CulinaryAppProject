/*
RecipeViewModel:
Наследуется от AndroidViewModel (для доступа к Application контексту, если нужно)
Содержит LiveData со списком рецептов (List<Meal>)
Загружает данные из API через ApiClient и обновляет LiveData
*/

package com.example.culinaryappproject.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.culinaryappproject.models.CategoriesResponse
import com.example.culinaryappproject.models.Category
import com.example.culinaryappproject.models.Meal
import com.example.culinaryappproject.models.RecipeResponse
import com.example.culinaryappproject.models.Recipe
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "RecipeViewModel"
    }

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _showRecipes = MutableLiveData(false)
    val showRecipes: LiveData<Boolean> get() = _showRecipes

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> get() = _isSearching

    private val combinedRecipes = mutableListOf<Recipe>()

    fun fetchCombinedRecipes() {
        combinedRecipes.clear()

        // 1. Загружаем из Firestore
        FirestoreRepository.getRecipesFromFirestore { firestoreRecipes ->
            combinedRecipes.addAll(firestoreRecipes)

            // Обновляем UI пока есть только Firestore
            _recipes.value = combinedRecipes.toList()

            // 2. Загружаем из внешнего API
//            ApiClient.apiService.getRecipes().enqueue(object : Callback<RecipeResponse> {
//                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
//                    if (response.isSuccessful) {
//                        val meals = response.body()?.meals ?: emptyList()
//
//                        val apiRecipes = meals.map { meal ->
//                            Recipe(
//                                id = meal.idMeal ?: "",
//                                userId = "api", // пометка, что из API
//                                title = meal.strMeal ?: "Без названия",
//                                photoUrl = meal.strMealThumb ?: "",
//                                cookingTime = 30,
//                                averageRating = 0.0,
//                                servings = 2,
//                                cuisine = "API",
//                                tags = listOf("из API"),
//                                ingredients = listOf(),
//                                steps = listOf(),
//                                reviews = listOf()
//                            )
//                        }
//
//                        combinedRecipes.addAll(apiRecipes)
//                        _recipes.postValue(combinedRecipes)
//                    } else {
//                        Log.e(TAG, "Ошибка получения рецептов из API: ${response.message()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
//                    Log.e(TAG, "Ошибка API-запроса: ${t.message}", t)
//                }
//            })
        }
    }

    fun fetchCategories() {
        ApiClient.apiService.getCategories().enqueue(object : Callback<CategoriesResponse> {
            override fun onResponse(call: Call<CategoriesResponse>, response: Response<CategoriesResponse>) {
                if (response.isSuccessful) {
                    _categories.value = response.body()?.categories ?: emptyList()
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                Log.e(TAG, "Ошибка загрузки категорий: ${t.message}", t)
            }
        })
    }

    fun fetchRecipesByCategory(category: String) {
        _showRecipes.value = true
        ApiClient.apiService.getRecipesByCategory(category).enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    val meals = response.body()?.meals ?: emptyList()
                    val recipes = meals.map {
                        Recipe(
                            id = it.idMeal ?: "",
                            userId = "api",
                            title = it.strMeal ?: "",
                            photoUrl = it.strMealThumb ?: "",
                            cuisine = category
                        )
                    }
                    _recipes.value = recipes
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Log.e(TAG, "Ошибка загрузки по категории: ${t.message}", t)
            }
        })
    }

    fun resetRecipes() {
        _showRecipes.value = false
        _recipes.value = emptyList()
    }

    fun searchRecipes(query: String) {
        if (query.isEmpty()) {
            _recipes.value = combinedRecipes
            return
        }

        if (query.length > 2) {
            ApiClient.apiService.searchMeals(query).enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                    if (response.isSuccessful) {
                        val meals = response.body()?.meals ?: emptyList()
                        val searchResults = meals.map {
                            Recipe(
                                id = it.idMeal ?: "",
                                userId = "api",
                                title = it.strMeal ?: "",
                                photoUrl = it.strMealThumb ?: "",
                                cuisine = "Поиск"
                            )
                        }
                        _recipes.value = searchResults
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    Log.e(TAG, "Ошибка поиска: ${t.message}", t)
                }
            })
        } else {
            val filtered = combinedRecipes.filter {
                it.title.contains(query, ignoreCase = true)
            }
            _recipes.value = filtered
        }
    }

    fun getRandomRecipe(): Recipe? {
        return _recipes.value?.randomOrNull()
    }

    fun updateRecipeFavoriteStatus(recipeId: String, isFavorite: Boolean) {
        val currentRecipes = _recipes.value ?: return
        val updatedRecipes = currentRecipes.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isFavorite = isFavorite)
            } else {
                recipe
            }
        }
        _recipes.value = updatedRecipes
    }

    private val _tags = MutableLiveData<List<String>>()
    val tags: LiveData<List<String>> get() = _tags

    fun fetchTags() {
        FirestoreRepository.getAllTags { tagList ->
            _tags.postValue(tagList)
        }
    }

    fun fetchRecipesByTag(tag: String) {
        FirestoreRepository.getRecipesFromFirestore { allRecipes ->
            val filtered = allRecipes.filter { it.tags.contains(tag) }
            _recipes.postValue(filtered)
            _showRecipes.postValue(true)
        }
    }

    private val _cuisines = MutableLiveData<List<String>>()
    val cuisines: LiveData<List<String>> get() = _cuisines

    fun fetchRecipesByTagAndCuisine(tag: String?, cuisine: String?) {
        FirestoreRepository.getRecipesFromFirestore { allRecipes ->
            val filtered = allRecipes.filter { recipe ->
                val matchesTag = tag == null || recipe.tags.contains(tag)
                val matchesCuisine = cuisine == null ||
                        recipe.cuisine.equals(cuisine, ignoreCase = true)
                matchesTag && matchesCuisine
            }

            // Если оба фильтра null - показываем пустой список или все рецепты
            if (tag == null && cuisine == null) {
                _showRecipes.postValue(false)
                _recipes.postValue(emptyList())
            } else {
                _recipes.postValue(filtered)
                _showRecipes.postValue(true)
            }
        }
    }

    fun fetchCuisines() {
        FirestoreRepository.getRecipesFromFirestore { recipes ->
            val cuisinesSet = recipes.mapNotNull { it.cuisine }
                .filter { it.isNotBlank() }
                .toSet()
                .sorted()
            _cuisines.postValue(cuisinesSet)
        }
    }

}
