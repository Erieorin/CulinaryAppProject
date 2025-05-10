/*
Интерфейс ApiService описывает:
Какие HTTP-запросы можно отправлять к API
Как параметры передаются в запрос
В каком формате возвращаются данные (через Call<T>)
*/

package com.example.culinaryappproject.network

import com.example.culinaryappproject.models.CategoriesResponse
import com.example.culinaryappproject.models.RecipeDetailResponse
import com.example.culinaryappproject.models.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // получение списка рецептов
    @GET("filter.php?a=Mexican") // фильтрация по кухне
    fun getRecipes(): Call<RecipeResponse> // Retrofit оборачивает ответ в Call, который содержит RecipeResponse (список блюд)

    @GET("lookup.php") // запрос к lookup.php для получения деталей блюда
    fun getRecipeDetails(@Query("i") mealId: String): Call<RecipeDetailResponse>
    /*
    @Query("i") mealId: String - добавляет параметр i=mealId к URL (например, lookup.php?i=52772).
    Ответ приходит в виде RecipeDetailResponse (детали одного блюда)
    */

    // категории
    @GET("categories.php")
    fun getCategories(): Call<CategoriesResponse>

    // фильтрация
    @GET("filter.php")
    fun getRecipesByCategory(@Query("c") category: String): Call<RecipeResponse>

    @GET("search.php")
    fun searchMeals(@Query("s") searchQuery: String): Call<RecipeResponse>
}