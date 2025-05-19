package com.example.culinaryappproject.models

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreRepository {

    private val db = Firebase.firestore

    fun saveUser(user: User) {
        db.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "User saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving user: $e")
            }
    }

    fun saveReview(review: Review) {
        db.collection("reviews")
            .document(review.id)
            .set(review)
            .addOnSuccessListener {
                Log.d("Firestore", "Review saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving review: $e")
            }
    }

    //сохранение рецетов
    fun saveRecipe(recipe: Recipe) {
        db.collection("recipes")
            .document(recipe.id)
            .set(recipe)
            .addOnSuccessListener { Log.d("Firestore", "Recipe saved") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error saving recipe", e) }
    }

    //отображение рецептов из собственной бд
    fun getRecipesFromFirestore(onResult: (List<Recipe>) -> Unit) {
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.documents.mapNotNull { it.toObject(Recipe::class.java) }
                onResult(recipes)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Ошибка при загрузке рецептов", e)
                onResult(emptyList())
            }
    }

    fun getRecipeById(recipeId: String, callback: (Recipe?) -> Unit) {
        db.collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener { snapshot ->
                val recipe = snapshot.toObject(Recipe::class.java)
                callback(recipe)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
