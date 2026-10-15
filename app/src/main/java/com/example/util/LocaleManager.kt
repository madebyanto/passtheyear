package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English 🇬🇧"),
    ITALIAN("it", "Italiano 🇮🇹"),
    SPANISH("es", "Español 🇪🇸")
}

class LanguageManager {
    // English is the default language
    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)

    fun setLanguage(lang: AppLanguage) {
        currentLanguage = lang
    }
}

val LocalAppLanguage = compositionLocalOf { LanguageManager() }
