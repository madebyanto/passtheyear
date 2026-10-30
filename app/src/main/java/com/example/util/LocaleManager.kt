package com.example.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English 🇬🇧"),
    ITALIAN("it", "Italiano 🇮🇹"),
    SPANISH("es", "Español 🇪🇸");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            if (code.isNullOrBlank()) return ENGLISH
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}

class LanguageManager(private val context: Context? = null) {
    private val prefs = context?.getSharedPreferences("app_language_prefs", Context.MODE_PRIVATE)

    var currentLanguage by mutableStateOf(loadInitialLanguage())
        private set

    // Callback invoked when language is modified by the user (to sync with Firestore)
    var onLanguageChangedListener: ((AppLanguage) -> Unit)? = null

    private fun loadInitialLanguage(): AppLanguage {
        val savedCode = prefs?.getString("selected_language", null)
        return AppLanguage.fromCode(savedCode)
    }

    fun setLanguage(lang: AppLanguage, syncToCloud: Boolean = true) {
        if (currentLanguage == lang && !syncToCloud) return
        currentLanguage = lang
        prefs?.edit()?.putString("selected_language", lang.code)?.apply()
        if (syncToCloud) {
            onLanguageChangedListener?.invoke(lang)
        }
    }
}

val LocalAppLanguage = compositionLocalOf { LanguageManager() }

