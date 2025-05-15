package com.example.culinaryappproject.models

data class Review(
    val id: String = "",
    val userId: String = "",
    val recipeId: String = "",
    val text: String = "",
    val rating: Int = 0
)