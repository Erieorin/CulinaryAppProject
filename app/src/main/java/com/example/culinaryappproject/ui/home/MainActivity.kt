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

import com.google.firebase.FirebaseApp
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        //Firestore: пользователь и отзыв
        val user = User(
            id = "abc123",
            name = "Иван",
            email = "ivan@example.com"
        )

        val review = Review(
            id = "rev123",
            userId = user.id,
            recipeId = "52772",
            text = "Вкусно!",
            rating = 5
        )
        Log.d("FirestoreTest", "Попытка сохранить пользователя и отзыв")

        val recipe = Recipe(
            id = "rec456",
            userId = user.id,
            title = "Чахохбили по-грузински",
            photoUrl = "https://example.com/images/chakhokhbili.jpg",
            cookingTime = 90,
            averageRating = 4.6,
            servings = 4,
            cuisine = "Грузинская",
            tags = listOf("острое", "тушёное", "курица"),
            ingredients = listOf(
                "1 кг курицы",
                "2 луковицы",
                "3 помидора"
            ),
            steps = listOf(
                Step(
                    title = "Этап 1",
                    description = "Нарезать курицу и обжарить.",
                    duration = 15
                ),
                Step(
                    title = "Этап 2",
                    description = "Добавить лук, тушить 10 минут.",
                    duration = 10
                )
            ),
            reviews = listOf(
                Review(
                    userId = "user_xyz789",
                    rating = 5,
                    text = "Очень вкусно!",
                )
            )
        )


        FirestoreRepository.saveRecipe(recipe)


        FirestoreRepository.saveUser(user)
        FirestoreRepository.saveReview(review)

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Наблюдение за данными рецептов
        recipeViewModel.recipes.observe(this) { meals ->
            val adapter = RecipeAdapter(this@MainActivity, meals)
            recyclerView.adapter = adapter
        }

        recipeViewModel.fetchRecipes()

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
        // Проверка разрешения для точных алермов (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Запросить разрешение
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, RecipeNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Устанавливаем на 10:00 утра
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            // Если уже 10:00, устанавливаем на завтра
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
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