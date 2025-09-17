package com.example.culinaryappproject.models

// используется для парсинга ответа от API, который возвращает детальную информацию о рецепте
data class RecipeDetailResponse(
    // поле meals содержит список объектов типа MealDetail (каждый из них описывает один рецепт)
    val meals: List<MealDetail>
)
