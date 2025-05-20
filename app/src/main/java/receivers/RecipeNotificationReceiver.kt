package com.example.culinaryappproject.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.culinaryappproject.R
import com.example.culinaryappproject.models.Recipe
import com.example.culinaryappproject.models.FirestoreRepository
import com.example.culinaryappproject.ui.details.RecipeDetailActivity
import com.example.culinaryappproject.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class RecipeNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Явно указываем тип возвращаемого значения
                val recipe: Recipe? = FirestoreRepository.getRandomRecipe()

                recipe?.let { randomRecipe ->
                    showNotification(context, randomRecipe)
                } ?: run {
                    // Логируем если рецепт не получен
                    android.util.Log.e("Notification", "Failed to get random recipe")
                }
            } catch (e: Exception) {
                android.util.Log.e("Notification", "Error in notification receiver", e)
            }
        }
    }

    private fun showNotification(context: Context, recipe: Recipe) {
        // 1. Создаем Intent для открытия деталей рецепта
        val detailIntent = Intent(context, RecipeDetailActivity::class.java).apply {
            putExtra("RECIPE_ID", recipe.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // 2. Создаем PendingIntent с явными флагами
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(1000), // Уникальный requestCode
            detailIntent,
            pendingIntentFlags
        )

        // 3. Строим уведомление
        val notification = NotificationHelper.buildNotification(
            context,
            context.getString(R.string.notification_title),
            context.getString(R.string.notification_message, recipe.title)
        ).apply {
            setContentIntent(pendingIntent)
        }.build()

        // 4. Показываем уведомление
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NotificationHelper.NOTIFICATION_ID, notification)
    }
}