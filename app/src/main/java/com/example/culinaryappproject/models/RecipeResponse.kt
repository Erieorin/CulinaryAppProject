package com.example.culinaryappproject.models

// data class для парсинга ответа от API, который возвращает список рецептов
data class RecipeResponse(
    // поле meals содержит список объектов типа Meal
    val meals: List<Meal>
)