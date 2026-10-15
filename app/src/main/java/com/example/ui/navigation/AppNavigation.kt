package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.EmailVerificationScreen
import com.example.ui.auth.ForgotPasswordScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.SignUpScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.months.MonthsVisionScreen
import com.example.ui.notes.NotesScreen
import com.example.ui.notes.NotesViewModel
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.user.UserViewModel
import com.example.ui.wellness.WellnessScreen
import com.example.util.LocalAppLanguage
import com.example.util.getTranslations

enum class AuthDestination {
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD
}

enum class MainTab(
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    DASHBOARD("nav_dashboard", Icons.Filled.Home, Icons.Outlined.Home, "tab_dashboard"),
    MONTHS("nav_months", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "tab_months"),
    NOTES("nav_notes", Icons.Filled.EditNote, Icons.Outlined.EditNote, "tab_notes"),
    WELLNESS("nav_wellness", Icons.Filled.Spa, Icons.Outlined.Spa, "tab_wellness"),
    SETTINGS("nav_settings", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    notesViewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)

    var authDestination by remember { mutableStateOf(AuthDestination.LOGIN) }
    var currentTab by remember { mutableStateOf(MainTab.DASHBOARD) }

    // When user authenticates, bind user to UserViewModel and NotesViewModel
    LaunchedEffect(authState.currentUserId, authState.isAuthenticated) {
        if (authState.isAuthenticated && authState.currentUserId != null) {
            userViewModel.bindUser(authState.currentUserId!!)
            notesViewModel.bindUser(authState.currentUserId!!)
        }
    }

    if (!authState.isAuthenticated) {
        // Intermediate email verification gate as required:
        if (authState.isVerificationRequired) {
            EmailVerificationScreen(
                viewModel = authViewModel,
                onBackToLogin = {
                    authDestination = AuthDestination.LOGIN
                }
            )
        } else {
            when (authDestination) {
                AuthDestination.LOGIN -> {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToSignUp = { authDestination = AuthDestination.SIGN_UP },
                        onNavigateToForgotPassword = { authDestination = AuthDestination.FORGOT_PASSWORD }
                    )
                }
                AuthDestination.SIGN_UP -> {
                    BackHandler { authDestination = AuthDestination.LOGIN }
                    SignUpScreen(
                        viewModel = authViewModel,
                        onNavigateBackToLogin = { authDestination = AuthDestination.LOGIN }
                    )
                }
                AuthDestination.FORGOT_PASSWORD -> {
                    BackHandler { authDestination = AuthDestination.LOGIN }
                    ForgotPasswordScreen(
                        viewModel = authViewModel,
                        onNavigateBack = { authDestination = AuthDestination.LOGIN }
                    )
                }
            }
        }
    } else {
        // Authenticated flow
        val profile = userState.profile
        val needsOnboarding = profile == null || !profile.onboardingCompleted || profile.schoolStartDate.isBlank() || profile.schoolEndDate.isBlank()

        if (userState.isLoading && profile == null) {
            // Loading profile splash
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (needsOnboarding) {
            // First run onboarding
            OnboardingScreen(
                userViewModel = userViewModel,
                onOnboardingFinished = {
                    currentTab = MainTab.DASHBOARD
                }
            )
        } else {
            // Main App with M3 Navigation Bar
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        MainTab.values().forEach { tab ->
                            val label = when (tab) {
                                MainTab.DASHBOARD -> strings.navDashboard
                                MainTab.MONTHS -> strings.navMonths
                                MainTab.NOTES -> strings.navNotes
                                MainTab.WELLNESS -> strings.navWellness
                                MainTab.SETTINGS -> strings.navSettings
                            }
                            val isSelected = currentTab == tab

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                },
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        MainTab.DASHBOARD -> {
                            DashboardScreen(
                                userViewModel = userViewModel,
                                onNavigateToMonths = { currentTab = MainTab.MONTHS },
                                onNavigateToWellness = { currentTab = MainTab.WELLNESS },
                                onNavigateToNotes = { currentTab = MainTab.NOTES },
                                onNavigateToSettings = { currentTab = MainTab.SETTINGS }
                            )
                        }
                        MainTab.MONTHS -> {
                            BackHandler { currentTab = MainTab.DASHBOARD }
                            MonthsVisionScreen(userViewModel = userViewModel)
                        }
                        MainTab.NOTES -> {
                            BackHandler { currentTab = MainTab.DASHBOARD }
                            NotesScreen(notesViewModel = notesViewModel)
                        }
                        MainTab.WELLNESS -> {
                            BackHandler { currentTab = MainTab.DASHBOARD }
                            WellnessScreen()
                        }
                        MainTab.SETTINGS -> {
                            BackHandler { currentTab = MainTab.DASHBOARD }
                            SettingsScreen(
                                userViewModel = userViewModel,
                                authViewModel = authViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
