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
            photoUrl = "https://eda.ru/images/RecipePhoto/1920x1440/chahohbili-iz-kuricy-po-gruzinski_92708_photo_122515.webp",
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

        fun createSampleRecipes(userId: String): List<Recipe> {
            return listOf(
                Recipe(
                    id = "rec4",
                    userId = userId,
                    title = "Спагетти карбонара со сливками",
                    photoUrl = "https://eda.ru/images/RecipePhoto/390x390/spagetti-karbonara-s-krasnym-lukom_17614_photo_57434.webp",
                    cookingTime = 20,
                    averageRating = 4.8,
                    servings = 4,
                    cuisine = "Итальянская",
                    tags = listOf("паста"),
                    ingredients = listOf(
                        "Сливочное масло - 20 г",
                        "Тертый сыр пармезан - 50 г",
                        "Куриное яйцо - 4 штуки",
                        "Молотый черный перец - по вкусу",
                        "Соль - по вкусу",
                        "Чеснок - 2 зубчика",
                        "Бекон - 50 г",
                        "Красный лук - 1 головка",
                        "Спагетти - 250 г",
                        "Сливки 20%-ные - 200 мл",
                    ),
                    steps = listOf(
                        Step(
                            "Этап 1",
                            "Вскипятите воду в большой кастрюле и сварите пасту до состояния аль денте.",
                            7
                        ),
                        Step(
                            "Этап 2",
                            "Растопите на сковороде масло и обжарьте на нем мелко нарезанные лук, чеснок и бекон.",
                            10
                        ),
                        Step(
                            "Этап 3",
                            "Снимите сковороду с огня и в глубокой миске взбейте четыре яичных желтка со сливками и тертым пармезаном.",
                            7
                        ),
                        Step(
                            "Этап 4",
                            "В готовые спагетти вывалите обжаренные с луком и чесноком кусочки бекона. Влейте смесь сливок, желтков и пармезана, перемешайте.",
                            3
                        )
                    ),
                    reviews = listOf(
                        Review(
                            userId = "user_xyz789",
                            rating = 5,
                            text = "Очень вкусно!",
                        )
                    )
                ),
                Recipe(
                    id = "rec5",
                    userId = userId,
                    title = "Брауни",
                    photoUrl = "https://eda.ru/images/RecipePhoto/390x390/brauni-brownie_20955_photo_29166.webp",
                    cookingTime = 40,
                    averageRating = 4.5,
                    servings = 6,
                    cuisine = "Американская",
                    tags = listOf("завтрак", "сладкое"),
                    ingredients = listOf(
                        "Сливочное масло - 180 г",
                        "Куриное яйцо - 4 штуки",
                        "Пшеничная мука - 100 г",
                        "Грецкие орехи - 100 г",
                        "Коричневый сахар - 200 г",
                        "Темный шоколад - 100 г",
                    ),
                    steps = listOf(
                        Step(
                            "Этап 1",
                            "Шоколад разломать на кусочки и вместе со сливочным маслом растопить на водяной бане, не переставая все время помешивать лопаткой или деревянной ложкой.",
                            10
                        ),
                        Step(
                            "Этап 2",
                            "Тем временем смешать яйца со ста граммами коричневого сахара: яйца разбить в отдельную миску и взбить, постепенно добавляя сахар.",
                            10
                        ),
                        Step(
                            "Этап 3",
                            "Острым ножом на разделочной доске порубить грецкие орехи.",
                            3
                        ),
                        Step(
                            "Этап 4",
                            "В остывший растопленный со сливочным маслом шоколад аккуратно добавить оставшийся сахар, затем муку и измельченные орехи и все хорошо перемешать венчиком.",
                            5
                        ),
                        Step(
                            "Этап 5",
                            "Затем влить сахарно-яичную смесь и тщательно смешать с шоколадной массой.",
                            2
                        ),
                        Step(
                            "Этап 6",
                            "Разогреть духовку до 200 градусов. Дно небольшой глубокой огнеупорной формы выстелить листом бумаги для выпечки или калькой. Перелить тесто в форму.",
                            5
                        ),
                        Step(
                            "Этап 7",
                            "Готовый пирог вытащить из духовки, дать остыть и нарезать на квадратики острым ножом или ножом для пиццы — так кусочки получатся особенно ровными.",
                            3
                        ),
                        Step(
                            "Этап 8",
                            "Подавать брауни можно просто так, а можно посыпать сверху сахарной пудрой или разложить квадратики по тарелкам и украсить каждую порцию шариком ванильного мороженого.",
                            2
                        ),
                    ),
                    reviews = listOf(
                        Review(
                            userId = "user_xyz789",
                            rating = 5,
                            text = "Очень вкусно!",
                        )
                    )
                ),
                Recipe(
                    id = "rec6",
                    userId = userId,
                    title = "Курица «Пикассо»",
                    photoUrl = "https://eda.ru/images/RecipePhoto/390x390/kurica-pikasso_25902_photo_9940.webp",
                    cookingTime = 45,
                    averageRating = 4.7,
                    servings = 4,
                    cuisine = "Американская",
                    tags = listOf("курица", "острое"),
                    ingredients = listOf(
                        "Вода - ½ стакана",
                        "Сливочное масло - 1 столовая ложка",
                        "Молотый черный перец - по вкусу",
                        "Соль - по вкусу",
                        "Сыр - 100 г",
                        "Чеснок - 3 зубчика",
                        "Помидоры - 4 штуки",
                        "Сладкий перец - 3 штуки",
                        "Сливки - ½ стакана",
                        "Оливковое масло - 2 столовые ложки",
                        "Мускатный орех - щепотка",
                        "Куриная грудка - 4 штуки",
                        "Лук - 2 штуки",
                        "Овощной бульонный кубик - 1 штука",
                        "Смесь итальянских трав - 1 столовая ложка",
                    ),
                    steps = listOf(
                        Step(
                            "Этап 1",
                            "Нарежьте болгарский перец кольцами (лучше выбрать трех разных цветов — это выглядит красочнее), предварительно удалив семена.",
                            10
                        ),
                        Step(
                            "Этап 2",
                            "Куриные грудки посолить, поперчить. В комбинации 2 столовые ложки оливкового и 1 столовая ложка сливочного масла, обжарить грудки до золотистой корочки.",
                            10
                        ),
                        Step(
                            "Этап 3",
                            "В этой же сковороде обжарить лук до золотистого цвета, переложить в форму к курице.",
                            3
                        ),
                        Step(
                            "Этап 4",
                            "Время для болгарского перца — слегка обжарить кольца, пока они не станут мягкими — и к курице.",
                            5
                        ),
                        Step(
                            "Этап 5",
                            "Тертый чеснок поместить в сковороду, пассеровать 30 секунд, затем залить водой, добавив нарезанные помидоры (кожицу можно предварительно удалить), хорошо перемешать.",
                            2
                        ),
                        Step(
                            "Этап 6",
                            "Варить соус 5 минут на медленном огне. Залить им курицу с овощами. Закрыть фольгой, отправить в духовку при температуре 200 градусов на 30 минут.",
                            5
                        ),
                        Step(
                            "Этап 7",
                            "Достать, посыпать натертым на терке сыром, поместить курицу обратно в духовку, но уже без фольги, еще на 15 минут, пока сыр не расплавится.",
                            3
                        ),
                    ),
                    reviews = listOf(
                        Review(
                            userId = "user_xyz789",
                            rating = 5,
                            text = "Очень вкусно!",
                        )
                    )
                ),
                Recipe(
                    id = "rec7",
                    userId = userId,
                    title = "Американский тыквенный пирог с корицей",
                    photoUrl = "https://eda.ru/images/RecipePhoto/390x390/amerikanskij-tikvennij-pirog-s-koricej_18811_photo_19337.webp",
                    cookingTime = 120,
                    averageRating = 4.9,
                    servings = 8,
                    cuisine = "Американская",
                    tags = listOf("сладкое", "пирог"),
                    ingredients = listOf(
                        "Сахар - 200 г",
                        "Сливочное масло - 250 г",
                        "Куриное яйцо - 3 штуки",
                        "Соль - по вкусу",
                        "Пшеничная мука - 400 г",
                        "Сливки 30%-ные - 200 мл",
                        "Корица - по вкусу",
                        "Ванилин - по вкусу",
                        "Тыква - 900 г",
                    ),
                    steps = listOf(
                        Step(
                            "Этап 1",
                            "Просеять муку и соль в глубокую миску. Перетереть между ладоней с мягким маслом, чтобы смесь напоминала хлебные крошки, затем добавить слегка взбитое яйцо и замесить тесто. Скатать его в шар, завернуть в пленку и отправить в холодильник на 30–50 минут.",
                            10
                        ),
                        Step(
                            "Этап 2",
                            "Очистить тыкву, удалить семена. Нарезать мякоть кубиками. Положить в кастрюлю с толстым дном или сотейник, добавить немного воды и тушить до мягкости и полного испарения жидкости. Блендером измельчить мякоть в однородную массу.",
                            10
                        ),
                        Step(
                            "Этап 3",
                            "Раскатать тесто на слегка присыпанной мукой поверхности и застелить им круглую низкую форму диаметром около 25 см. Поверх теста положить лист бумаги для запекания и засыпать любую крупу. Это позволит коржу равномерно пропечься. Выпекать в течение 15 минут при 190 градусах.",
                            3
                        ),
                        Step(
                            "Этап 4",
                            "Выложить тыквенное пюре в глубокую миску, взбить с двумя яйцами, сахаром, сливками, пряностями и солью. Вылить смесь в форму с запеченным тестом. Выпекать пирог 50–55 минут при 180 градусах.",
                            5
                        ),

                    ),
                    reviews = listOf(
                        Review(
                            userId = "user_xyz789",
                            rating = 5,
                            text = "Очень вкусно!",
                        )
                    )
                ),
                Recipe(
                    id = "rec8",
                    userId = userId,
                    title = "Оякодон (японский омлет с рисом и курицей)",
                    photoUrl = "https://eda.ru/images/RecipePhoto/390x390/ojakodon-japonskij-omlet-s-risom-kuricej_28425_photo_29054.webp",
                    cookingTime = 30,
                    averageRating = 4.3,
                    servings = 2,
                    cuisine = "Японская",
                    tags = listOf("завтрак", "курица"),
                    ingredients = listOf(
                        "Сахар - 2 столовые ложки",
                        "Репчатый лук - 1 головка",
                        "Куриное яйцо - 3 штуки",
                        "Рис - ½ стакана",
                        "Зеленый лук - 20 г",
                        "Куриное филе - 300 г",
                        "Соевый соус - 6 столовых ложек",
                    ),
                    steps = listOf(
                        Step(
                            "Этап 1",
                            "Репчатый лук очистить и нарезать тонкими полукольцами. На среднем огне разогреть сковороду и влить в нее шесть столовых ложек соевого соуса.",
                            10
                        ),
                        Step(
                            "Этап 2",
                            "Как только соевый соус начнет кипеть, бросить в сковороду луковые полукольца и посыпать сверху сахаром.",
                            10
                        ),
                        Step(
                            "Этап 3",
                            "Тем временем куриное филе нарезать небольшими кусочками — сильно измельчать мясо не нужно, готовые ломтики должны остаться сочными и плотными.",
                            3
                        ),
                        Step(
                            "Этап 4",
                            "Положить курицу в сковороду и перемешать с соусом. Когда мясо побелеет с одной стороны, перевернуть аккуратно ломтики и потушить еще пару минут.",
                            5
                        ),
                        Step(
                            "Этап 5",
                            "В отдельной миске вилкой или венчиком быстро взбить яйца в однородную смесь. Добавлять соль при этом не нужно — соевый соус, в котором тушится мясо, и так достаточно солон.",
                            2
                        ),
                        Step(
                            "Этап 6",
                            "Вылить яичную смесь на сковороду, стараясь равномерно покрыть все мясо.",
                            5
                        ),
                        Step(
                            "Этап 7",
                            "Зеленый лук измельчить. На сервировочную тарелку выложить горкой теплый рис, сваренный на пару.",
                            3
                        ),
                        Step(
                            "Этап 8",
                            "Сверху на рис аккуратно выложить омлет — для удобства его можно разделить лопаткой на треугольные сегменты.",
                            2
                        ),
                    ),
                    reviews = listOf(
                        Review(
                            userId = "user_xyz789",
                            rating = 5,
                            text = "Очень вкусно!",
                        )
                    )
                ),
                Recipe(
                    id = "rec9",
                    userId = userId,
                    title = "Блины тонкие на кипятке и молоке",
                    photoUrl = "https://eda.ru/images/RecipePhoto/390x390/blini-tonkie-na-kipjatke-i-moloke_42947_photo_146641.webp",
                    cookingTime = 15,
                    averageRating = 4.99,
                    servings = 2,
                    cuisine = "Русская",
                    tags = listOf("завтрак", "тесто"),
                    ingredients = listOf(
                        "Куриное яйцо - 2 штуки",
                        "Соль - по вкусу",
                        "Растительное масло - 2 столовые ложки",
                        "Молоко - 1 стакан",
                        "Пшеничная мука - 1 стакан",
                        "Кипяток - 1 стакан",
                    ),
                    steps = listOf(
                        Step(
                            "Этап 1",
                            "Взбить в пену яйца с солью.",
                            10
                        ),
                        Step(
                            "Этап 2",
                            "Не переставая взбивать, влить стакан кипятка.",
                            10
                        ),
                        Step(
                            "Этап 3",
                            "Не переставая взбивать, влить стакан холодного молока.",
                            3
                        ),
                        Step(
                            "Этап 4",
                            "Всыпать 1 стакан муки.",
                            5
                        ),
                        Step(
                            "Этап 5",
                            "В тесто добавить растительное масло.",
                            2
                        ),
                        Step(
                            "Этап 6",
                            "Печь на горячей сковородке, смазанной оливковым или топленым сливочным маслом.",
                            5
                        ),
                    ),
                    reviews = listOf(
                        Review(
                            userId = "user_xyz789",
                            rating = 5,
                            text = "Очень вкусно!",
                        )
                    )
                ),
                Recipe(
                    id = "rec10",
                    userId = userId,
                    title = "Шарлотка традиционная",
                    photoUrl = "https://eda.ru/images/RecipePhoto/390x390/sharlotka-tradicionnaja_21158_photo_83332.webp",
                    cookingTime = 30,
                    averageRating = 4.8,
                    servings = 4,
                    cuisine = "Русская",
                    tags = listOf("завтрак", "тесто"),
                    ingredients = listOf(
                        "Сахар - 1 стакан",
                        "Куриное яйцо - 4 штуки",
                        "Соль - на кончике ножа",
                        "Яблоко - 1 кг",
                        "Пшеничная мука - 1 стакан",
                        "Сода - ½ чайные ложки",
                    ),
                    steps = listOf(
                        Step(
                            "Этап 1",
                            "Белки взбить с половиной стакана сахара.",
                            10
                        ),
                        Step(
                            "Этап 2",
                            "Желтки взбить с оставшимся сахаром.",
                            10
                        ),
                        Step(
                            "Этап 3",
                            "Соединить все вместе и постепенно добавить муку.",
                            3
                        ),
                        Step(
                            "Этап 4",
                            "Добавить соль и соду.",
                            5
                        ),
                        Step(
                            "Этап 5",
                            "Добавить нарезанные кубиками яблоки.",
                            2
                        ),
                        Step(
                            "Этап 6",
                            "Форму для выпекания смазать маслом и посыпать манной крупой.",
                            5
                        ),
                        Step(
                            "Этап 7",
                            "Выложить массу в форму и поставить в духовку, разогретую до 180 градусов.",
                            5
                        ),
                        Step(
                            "Этап 8",
                            "Выпекать 30-40 минут.",
                            5
                        ),
                    ),
                    reviews = listOf(
                        Review(
                            userId = "user_xyz789",
                            rating = 5,
                            text = "Очень вкусно!",
                        )
                    )
                ),
            )
        }

        val recipes = createSampleRecipes(user.id)
        recipes.forEach { recipe ->
            FirestoreRepository.saveRecipe(recipe)
        }


        FirestoreRepository.saveRecipe(recipe)
        FirestoreRepository.saveUser(user)
        FirestoreRepository.saveReview(review)

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        /* Наблюдение за данными рецептов
        recipeViewModel.recipes.observe(this) { meals ->
            val adapter = RecipeAdapter(this@MainActivity, meals)
            recyclerView.adapter = adapter
        }
        */
        recipeViewModel.recipes.observe(this) { recipes ->
            val userId = user.id
            val adapter = RecipeAdapter(this@MainActivity, recipes, userId)
            recyclerView.adapter = adapter

            // проверяем статус избранного для каждого рецепта
            recipes.forEach { recipe ->
                FirestoreRepository.checkIfFavorite(userId, recipe.id) { isFavorite ->
                    recipe.isFavorite = isFavorite
                    adapter.notifyDataSetChanged()
                }
            }
        }
        //recipeViewModel.fetchRecipes()
        //recipeViewModel.fetchRecipesFromFirestore()
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