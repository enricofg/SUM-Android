package com.example.sum.utility

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

object LocaleHelper {
    fun updateBaseContextLocale(context: Context): Context {
        val language = getLanguageFromPreferences(context)
        val locale = Locale(language)
        Locale.setDefault(locale)
        return updateResourcesLocale(context, locale)
    }

    private fun updateResourcesLocale(
        context: Context,
        locale: Locale
    ): Context {
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        updateAppTheme(context)
        return context.createConfigurationContext(configuration)
    }
    
    private fun updateAppTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)
        when (sharedPreferences.get("theme", "auto")) {
            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            "auto" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun getLanguageFromPreferences(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)
        return sharedPreferences.get("language", "en")
    }
}