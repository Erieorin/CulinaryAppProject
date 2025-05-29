package com.example.culinaryappproject.models

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    suspend fun getRandomRecipe(): Recipe? {
        return try {
            val querySnapshot = Firebase.firestore.collection("recipes").get().await()
            val recipes = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Recipe::class.java)?.copy(id = document.id)
            }
            recipes.randomOrNull()
        } catch (e: Exception) {
            null
        }
    }

    // избранное
    private const val FAVORITES_COLLECTION = "favorites"

    fun addToFavorites(userId: String, recipeId: String, onComplete: (Boolean) -> Unit) {
        val favorite = FavoriteRecipe(userId = userId, recipeId = recipeId)

        db.collection(FAVORITES_COLLECTION)
            .document("${userId}_${recipeId}") // id для связи пользователь-рецепт
            .set(favorite)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun removeFromFavorites(userId: String, recipeId: String, onComplete: (Boolean) -> Unit) {
        db.collection(FAVORITES_COLLECTION)
            .document("${userId}_${recipeId}")
            .delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun checkIfFavorite(userId: String, recipeId: String, onComplete: (Boolean) -> Unit) {
        db.collection(FAVORITES_COLLECTION)
            .document("${userId}_${recipeId}")
            .get()
            .addOnSuccessListener { document ->
                onComplete(document.exists())
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun getFavoriteRecipes(userId: String, onComplete: (List<String>) -> Unit) {
        db.collection(FAVORITES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipeIds = querySnapshot.documents.map { it.getString("recipeId") ?: "" }
                onComplete(recipeIds)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }
}
