package com.example.culinaryappproject.models

data class Recipe(
    val id: String = "", //id самого рецепта (можно генерировать UUID или использовать Firestore ID)
    val userId: String = "", //id пользователя, кто выложил
    val title: String = "", //название рецепта
    val photoUrl: String = "", //фото блюда (ссылка)
    val cookingTime: Int = 0, //общее время приготовления в минутах
    val averageRating: Double = 0.0, //средняя оценка (по отзывам)
    val servings: Int = 1, //на сколько порций
    val cuisine: String = "", //кухня (например, "итальянская", "японская")
    val tags: List<String> = emptyList(), //теги (например, "веганское", "быстро", "остро")
    val ingredients: List<String> = emptyList(), //список ингредиентов (просто строками)

    val steps: List<Step> = emptyList(), //этапы приготовления
    val reviews: List<Review> = emptyList(), //отзывы к рецепту
    var isFavorite: Boolean = false
)

