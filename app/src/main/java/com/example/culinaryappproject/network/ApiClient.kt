/*
Класс ApiClient — это синглтон (объект Kotlin), который создаёт единственный экземпляр Retrofit и
предоставляет готовый apiService для выполнения запросов
Retrofit — это популярная библиотека для Android и Kotlin/Java, которая превращает ваш REST API
в удобный Kotlin/Java-интерфейс.
Проще говоря:
1. Она превращает HTTP-запросы в методы Kotlin/Java.
2. Автоматически преобразует JSON-ответы в объекты (например, Recipe, User и т. д.).
3. Работает с OkHttp (библиотека для сетевых запросов).
*/

package com.example.culinaryappproject.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// базовая ссылка API, все запросы будут относительными к этому адресу
private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
// настройка retrofit
object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // указываем базовый URL
        .addConverterFactory(GsonConverterFactory.create()) // добавляем конвертер Gson
        .client(OkHttpClient.Builder().build()) // настраиваем HTTP-клиент
        .build()

    // создание API-сервиса
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

/*
GsonConverterFactory — преобразует JSON-ответы API в Kotlin-объекты
OkHttpClient — HTTP-клиент для обработки запросов
ApiService — это интерфейс с объявленными API-методами (например, getRecipes())
Retrofit автоматически генерирует реализацию этого интерфейса
*/