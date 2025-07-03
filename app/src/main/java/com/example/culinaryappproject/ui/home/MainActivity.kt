package com.example.culinaryappproject.ui.home

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
import com.example.culinaryappproject.ui.search.SearchActivity
import com.example.culinaryappproject.receivers.RecipeNotificationReceiver
import java.util.Calendar
import com.example.culinaryappproject.models.User
import com.example.culinaryappproject.models.Review
import com.example.culinaryappproject.models.Recipe
import com.example.culinaryappproject.models.Step
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.ui.register.RegisterActivity
import com.example.culinaryappproject.ui.profile.ProfileActivity
import com.example.culinaryappproject.ui.favorites.FavoritesActivity
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.FirebaseApp
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recipeViewModel.recipes.observe(this) { recipes ->
            val adapter = RecipeAdapter(this@MainActivity, recipes, userId.toString())
            recyclerView.adapter = adapter

            // проверяем статус избранного для каждого рецепта
            recipes.forEach { recipe ->
                if (userId != null) {
                    FirestoreRepository.checkIfFavorite(userId, recipe.id) { isFavorite ->
                        recipe.isFavorite = isFavorite
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
        recipeViewModel.fetchCombinedRecipes()

        // Поиск
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                recipeViewModel.searchRecipes(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                recipeViewModel.searchRecipes(newText)
                return false
            }
        })

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> true
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
                R.id.navigation_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_home


        // Проверка и запрос разрешений для уведомлений (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Настройка ежедневных уведомлений
        setupDailyNotification()
    }

    private fun setupDailyNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
}

/*
Activity запускается → вызывает fetchRecipes() во ViewModel.
ViewModel обращается к API
Полученные данные сохраняются в LiveData/StateFlow (поле recipes)
RecyclerView обновляется через Observer (адаптер получает новые meals)
*/