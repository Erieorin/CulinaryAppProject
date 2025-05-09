package com.example.culinaryappproject.models

data class MealDetail(
    val dateModified: Any, // дата изменения (может быть string или null)
    val idMeal: String,
    val strArea: String?, // вид кухни
    val strCategory: String?,
    val strCreativeCommonsConfirmed: Any?, // подтверждение лицензии
    val strDrinkAlternate: Any?, // альтернативный напиток
    val strImageSource: Any?,
    val strIngredient1: String?, // ингредиент
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    val strIngredient10: String?,
    val strIngredient11: String?,
    val strIngredient12: String?,
    val strIngredient13: String?,
    val strIngredient14: String?,
    val strIngredient15: String?,
    val strIngredient16: String?,
    val strIngredient17: String?,
    val strIngredient18: String?,
    val strIngredient19: String?,
    val strIngredient20: String?,
    val strInstructions: String?, // пошаговый рецепт
    val strMeal: String?,
    val strMealThumb: String?, // URL изображения
    val strMeasure1: String?, //количество ингредиента
    val strMeasure2: String?,
    val strMeasure3: String?,
    val strMeasure4: String?,
    val strMeasure5: String?,
    val strMeasure6: String?,
    val strMeasure7: String?,
    val strMeasure8: String?,
    val strMeasure9: String?,
    val strMeasure10: String?,
    val strMeasure11: String?,
    val strMeasure12: String?,
    val strMeasure13: String?,
    val strMeasure14: String?,
    val strMeasure15: String?,
    val strMeasure16: String?,
    val strMeasure17: String?,
    val strMeasure18: String?,
    val strMeasure19: String?,
    val strMeasure20: String?,
    val strSource: String?, // источник рецепта (URL)
    val strTags: Any?, // теги (может быть String или null)
    val strYoutube: String?
)
