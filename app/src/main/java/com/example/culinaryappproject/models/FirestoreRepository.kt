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
                val recipe = snapshot.toObject(Recipe::class.java)?.copy(id = snapshot.id)
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

    fun getReviewsWithUserNames(
        recipeId: String,
        callback: (List<Pair<String, Review>>) -> Unit
    ) {
        val recipeRef = db.collection("recipes").document(recipeId)

        recipeRef.get().addOnSuccessListener { recipeDoc ->
            val rawReviews = recipeDoc.get("reviews") as? List<Map<String, Any>> ?: emptyList()
            val resultList = mutableListOf<Pair<String, Review>>()

            if (rawReviews.isEmpty()) {
                callback(emptyList())
                return@addOnSuccessListener
            }

            var processed = 0
            for (reviewMap in rawReviews) {
                val review = Review(
                    id = reviewMap["id"] as? String ?: "",
                    userId = reviewMap["userId"] as? String ?: "",
                    recipeId = reviewMap["recipeId"] as? String ?: "",
                    text = reviewMap["text"] as? String ?: "",
                    rating = (reviewMap["rating"] as? Number)?.toInt() ?: 0
                )

                if (review.userId.isNotEmpty()) {
                    db.collection("users").document(review.userId).get()
                        .addOnSuccessListener { userDoc ->
                            val name = userDoc.getString("name") ?: "Аноним"
                            resultList.add(name to review)
                            processed++
                            if (processed == rawReviews.size) {
                                callback(resultList)
                            }
                        }
                        .addOnFailureListener {
                            resultList.add("Аноним" to review)
                            processed++
                            if (processed == rawReviews.size) {
                                callback(resultList)
                            }
                        }
                } else {
                    resultList.add("Аноним" to review)
                    processed++
                    if (processed == rawReviews.size) {
                        callback(resultList)
                    }
                }
            }
        }.addOnFailureListener {
            callback(emptyList())
        }
    }

    fun getAllTags(callback: (List<String>) -> Unit) {
        db.collection("recipes")
            .get()
            .addOnSuccessListener { snapshot ->
                val allTags = snapshot.documents.flatMap { document ->
                    val tags = document.get("tags") as? List<String>
                    tags ?: emptyList()
                }
                val uniqueTags = allTags.toSet().toList()
                callback(uniqueTags)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

}
