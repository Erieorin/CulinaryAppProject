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

/*
class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData
    //private val _recipes = MutableLiveData<List<Meal>>() // для внутреннего использования (изменяемая версия)
    //val recipes: LiveData<List<Meal>> get() = _recipes // для наблюдения из UI (неизменяемая версия)

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _showRecipes = MutableLiveData<Boolean>(false)
    val showRecipes: LiveData<Boolean> get() = _showRecipes

    companion object {
        private const val TAG = "RecipeViewModel"
    }

    private val _allRecipes = mutableListOf<Meal>() // Храним полный список

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> get() = _isSearching

    fun searchRecipes(query: String) {
        if (query.isEmpty()) {
            _recipes.value = _allRecipes
            return
        }
        if (query.length > 2) { // Ищем по API только если запрос длинный
            ApiClient.apiService.searchMeals(query).enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                    if (response.isSuccessful) {
                        _recipes.value = response.body()?.meals ?: emptyList()
                    }
                }
                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    Log.e(TAG, "Ошибка поиска: ${t.message}", t)
                }
            })
        } else if (query.isEmpty()) {
            _recipes.value = _allRecipes // Возвращаем полный список
        } else {
            // Локальный поиск по уже загруженным данным
            val filtered = _allRecipes.filter {
                it.strMeal?.contains(query, ignoreCase = true) == true
            }
            _recipes.value = filtered
        }
    }

    //из нашей бд
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    fun fetchRecipesFromFirestore() {
        FirestoreRepository.getRecipesFromFirestore { recipeList ->
            _recipes.value = recipeList
        }
    }


    // загрузка данных
    fun fetchRecipes() {
        // вызывает API-метод getRecipes() через Retrofit
        ApiClient.apiService.getRecipes().enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                // при успешном ответе обновляет _recipes
                if (response.isSuccessful) {
                    val meals = response.body()?.meals ?: emptyList()
                    _allRecipes.clear()
                    _allRecipes.addAll(meals)
                    _recipes.value = response.body()?.meals ?: emptyList()
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Log.e(TAG, "Ошибка загрузки: ${t.message}", t)
            }
        })
    }

    // вызывает рецепты по категориям
    fun fetchRecipesByCategory(category: String) {
        _showRecipes.value = true
        ApiClient.apiService.getRecipesByCategory(category).enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    _recipes.value = response.body()?.meals ?: emptyList()
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Log.e(TAG, "Ошибка загрузки рецептов по категории: ${t.message}", t)
            }
        })
    }

    // вызывает все категории которые есть в базе данных
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

    fun resetRecipes() {
        _showRecipes.value = false
        _recipes.value = emptyList()
    }

    fun getRandomRecipe(): Meal? {
        return _allRecipes.randomOrNull()
    }
}

*/


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
            ApiClient.apiService.getRecipes().enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                    if (response.isSuccessful) {
                        val meals = response.body()?.meals ?: emptyList()

                        val apiRecipes = meals.map { meal ->
                            Recipe(
                                id = meal.idMeal ?: "",
                                userId = "api", // пометка, что из API
                                title = meal.strMeal ?: "Без названия",
                                photoUrl = meal.strMealThumb ?: "",
                                cookingTime = 30,
                                averageRating = 0.0,
                                servings = 2,
                                cuisine = "API",
                                tags = listOf("из API"),
                                ingredients = listOf(),
                                steps = listOf(),
                                reviews = listOf()
                            )
                        }

                        combinedRecipes.addAll(apiRecipes)
                        _recipes.postValue(combinedRecipes)
                    } else {
                        Log.e(TAG, "Ошибка получения рецептов из API: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    Log.e(TAG, "Ошибка API-запроса: ${t.message}", t)
                }
            })
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
}
