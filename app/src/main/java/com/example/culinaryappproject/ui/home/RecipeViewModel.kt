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
import com.example.culinaryappproject.models.Meal
import com.example.culinaryappproject.models.RecipeResponse
import com.example.culinaryappproject.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData
    private val _recipes = MutableLiveData<List<Meal>>() // для внутреннего использования (изменяемая версия)
    val recipes: LiveData<List<Meal>> get() = _recipes // для наблюдения из UI (неизменяемая версия)

    companion object {
        private const val TAG = "RecipeViewModel"
    }

    // загрузка данных
    fun fetchRecipes() {
        // вызывает API-метод getRecipes() через Retrofit
        ApiClient.apiService.getRecipes().enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                // при успешном ответе обновляет _recipes
                if (response.isSuccessful) {
                    _recipes.value = response.body()?.meals ?: emptyList()
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Log.e(TAG, "Ошибка загрузки: ${t.message}", t)
            }
        })
    }
}