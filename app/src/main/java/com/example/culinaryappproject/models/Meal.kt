package com.example.culinaryappproject.models

// data class - специальный класс в Kotlin для хранения данных
data class Meal(
    val idMeal: String, // id блюда
    val strMeal: String, // название
    val strMealThumb: String // ссылка на изображение
)

/*
Что делает data class?
Автоматически генерирует методы:
toString() – красиво выводит данные
equals() и hashCode() – для сравнения объектов
copy() – создает копию с возможностью изменения полей
Используется для хранения данных (например, ответа от API).
*/