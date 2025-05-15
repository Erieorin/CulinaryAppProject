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
}
