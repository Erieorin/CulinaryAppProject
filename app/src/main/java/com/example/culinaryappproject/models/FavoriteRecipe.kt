package com.example.culinaryappproject.models

data class FavoriteRecipe(
    val userId: String = "",
    val recipeId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)