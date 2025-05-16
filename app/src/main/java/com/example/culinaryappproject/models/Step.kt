package com.example.culinaryappproject.models

data class Step(
    val title: String = "", //название этапа (например, "Этап 1")
    val description: String = "", //текст этапа
    val duration: Int = 0 //время на этап в минутах
)
