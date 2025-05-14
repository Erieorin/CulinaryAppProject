package com.example.culinaryappproject.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.culinaryappproject.R

class NotificationHelper(private val context: Context) {
    private val CHANNEL_ID = "recipe_channel"
    private val NOTIFICATION_ID = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showDailyRecipeNotification(mealName: String) {
        // Проверяем разрешение перед показом уведомления
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chef_hat)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_message, mealName))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            try {
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // Обработка случая, когда нет разрешения
                e.printStackTrace()
            }
        }
    }
}