package com.example.culinaryappproject.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.culinaryappproject.R
import com.example.culinaryappproject.models.User
import com.example.culinaryappproject.ui.favorites.FavoritesActivity
import com.example.culinaryappproject.ui.home.MainActivity
import com.example.culinaryappproject.ui.profile.ProfileActivity
import com.example.culinaryappproject.ui.search.SearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.culinaryappproject.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.length < 6) {
                Toast.makeText(this, "Проверьте корректность данных", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Регистрация пользователя через FirebaseAuth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                    val user = User(id = uid, name = name, email = email)

                    // Сохранение данных пользователя в Firestore
                    db.collection("users").document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                            // Переход на главный экран
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Register", "Ошибка Firestore: $e")
                            Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка регистрации: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Ошибка: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Нижняя навигация
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    finish()
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    finish()
                    true
                }
                R.id.navigation_register -> true
                else -> false
            }
        }

        bottomNav.selectedItemId = R.id.navigation_register
    }
}

