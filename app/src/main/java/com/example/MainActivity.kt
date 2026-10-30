package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.AuthViewModel
import com.example.ui.navigation.AppNavigation
import com.example.ui.notes.NotesViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.user.UserViewModel
import com.example.util.LanguageManager
import com.example.util.LocalAppLanguage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val languageManager = remember { LanguageManager(applicationContext) }

            CompositionLocalProvider(LocalAppLanguage provides languageManager) {
                MyApplicationTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val authViewModel: AuthViewModel = viewModel()
                        val userViewModel: UserViewModel = viewModel()
                        val notesViewModel: NotesViewModel = viewModel()

                        AppNavigation(
                            authViewModel = authViewModel,
                            userViewModel = userViewModel,
                            notesViewModel = notesViewModel
                        )
                    }
                }
            }
        }
    }
}
