package com.example.culinaryappproject.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.culinaryappproject.R
import com.example.culinaryappproject.ui.home.MainActivity
import com.example.culinaryappproject.ui.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, RegisterFragment())
                .commit()
        }
    }

    fun navigateToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, LoginFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToMainApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}