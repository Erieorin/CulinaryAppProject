package com.example.culinaryappproject.ui.home

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import androidx.appcompat.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.culinaryappproject.receivers.RecipeNotificationReceiver
import java.util.Calendar
import com.example.culinaryappproject.models.User
import com.example.culinaryappproject.models.Review
import com.example.culinaryappproject.models.Recipe
import com.example.culinaryappproject.models.Step
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.ui.profile.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.FirebaseApp
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culinaryappproject.ui.favorites.FavoritesFragment
import com.example.culinaryappproject.ui.register.AuthActivity
import com.example.culinaryappproject.ui.register.RegisterFragment
import com.example.culinaryappproject.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // Проверка и запрос разрешений для уведомлений (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Настройка ежедневных уведомлений
        setupDailyNotification()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        setupBottomNavigation();
    }

    private fun setupDailyNotification() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, RecipeNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Устанавливаем на 10:00 утра
        Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Разрешение получено, можно настраивать уведомления
                    setupDailyNotification()
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SearchFragment())
                        .commit()
                    true
                }
                R.id.nav_favorites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FavoritesFragment())
                        .commit()
                    true
                }
                R.id.navigation_register -> {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ProfileFragment())
                            .commit()
                    } else {
                        startActivity(Intent(this, AuthActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }
}

/*
Activity запускается → вызывает fetchRecipes() во ViewModel.
ViewModel обращается к API
Полученные данные сохраняются в LiveData/StateFlow (поле recipes)
RecyclerView обновляется через Observer (адаптер получает новые meals)
*/