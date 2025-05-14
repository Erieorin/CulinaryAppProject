package com.example.culinaryappproject.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.culinaryappproject.utils.NotificationHelper
import com.example.culinaryappproject.ui.home.RecipeViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider

class RecipeNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Получаем Application контекст
        val application = context.applicationContext as Application

        // Создаем ViewModel
        val viewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(RecipeViewModel::class.java)

        // Получаем случайный рецепт
        val randomRecipe = viewModel.getRandomRecipe()

        // Показываем уведомление
        randomRecipe?.let {
            NotificationHelper(context).showDailyRecipeNotification(it.strMeal)
        }
    }
}