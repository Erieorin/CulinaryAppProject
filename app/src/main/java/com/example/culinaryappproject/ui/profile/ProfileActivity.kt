package com.example.culinaryappproject.ui.profile

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.culinaryappproject.R
import com.google.firebase.auth.FirebaseAuth
import com.example.culinaryappproject.ui.register.RegisterActivity
import android.content.Intent
import com.example.culinaryappproject.ui.home.MainActivity
import com.example.culinaryappproject.ui.favorites.FavoritesActivity
import com.example.culinaryappproject.ui.search.SearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button



class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //получаем email пользователя
        val user = FirebaseAuth.getInstance().currentUser
        findViewById<TextView>(R.id.userEmailTextView).text = user?.email ?: "Неизвестно"

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, RegisterActivity::class.java) // или LoginActivity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

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