package com.example.culinaryappproject.ui.profile

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.culinaryappproject.R
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //получаем email пользователя
        val user = FirebaseAuth.getInstance().currentUser
        findViewById<TextView>(R.id.userEmailTextView).text = user?.email ?: "Неизвестно"
    }
}