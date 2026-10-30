package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.auth.AuthViewModel
import com.example.ui.components.AppTopBar
import com.example.ui.components.DatePickerField
import com.example.ui.components.InfoBanner
import com.example.ui.user.UserViewModel
import com.example.util.AppLanguage
import com.example.util.LocalAppLanguage
import com.example.util.SchoolCalendarUtils
import com.example.util.getTranslations

enum class SettingsSection(val testId: String) {
    ACADEMIC_DATES("academic"),
    LANGUAGE("language"),
    ACCOUNT("account"),
    ABOUT("about")
}

@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    initialSection: SettingsSection? = null
) {
    val userState by userViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val languageManager = LocalAppLanguage.current
    val strings = getTranslations(languageManager.currentLanguage)

    var selectedSection by remember { mutableStateOf<SettingsSection?>(initialSection) }

    var startDate by remember(userState.profile) {
        mutableStateOf(userState.profile?.schoolStartDate ?: "2026-09-12")
    }
    var endDate by remember(userState.profile) {
        mutableStateOf(userState.profile?.schoolEndDate ?: "2027-06-09")
    }
    var saturdayFree by remember(userState.profile) {
        mutableStateOf(userState.profile?.saturdayFree ?: true)
    }
    var validationError by remember { mutableStateOf<String?>(null) }
    var showSignOutConfirm by remember { mutableStateOf(false) }

    // Intercept back button when inside a specific section to return to sections menu
    BackHandler(enabled = selectedSection != null) {
        selectedSection = null
    }

    val topBarTitle = when (selectedSection) {
        SettingsSection.ACADEMIC_DATES -> strings.academicDatesSection
        SettingsSection.LANGUAGE -> strings.languageSection
        SettingsSection.ACCOUNT -> strings.accountSection
        SettingsSection.ABOUT -> strings.aboutAppSection
        null -> strings.settingsTitle
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = topBarTitle,
                canNavigateBack = selectedSection != null,
                onNavigateBack = { selectedSection = null }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Global feedback banners
            if (validationError != null) {
                InfoBanner(
                    message = validationError!!,
                    isError = true,
                    onDismiss = { validationError = null }
                )
            }

            if (userState.error != null) {
                InfoBanner(
                    message = userState.error!!,
                    isError = true,
                    onDismiss = { userViewModel.clearMessages() }
                )
            }

            if (userState.successMessage != null) {
                InfoBanner(
                    message = userState.successMessage!!,
                    isError = false,
                    onDismiss = { userViewModel.clearMessages() }
                )
            }

            AnimatedContent(
                targetState = selectedSection,
                transitionSpec = {
                    if (targetState != null && initialState == null) {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> width / 3 } + fadeIn(animationSpec = tween(220)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(220)) { width -> -width / 3 } + fadeOut(animationSpec = tween(180)))
                    } else if (targetState == null && initialState != null) {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> -width / 3 } + fadeIn(animationSpec = tween(220)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(220)) { width -> width / 3 } + fadeOut(animationSpec = tween(180)))
                    } else {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                    }
                },
                label = "settings_section_transition",
                modifier = Modifier.fillMaxWidth()
            ) { currentSection ->
                if (currentSection == null) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // ==================== MAIN SECTIONS MENU ====================
                        Text(
                            text = strings.settingsSectionsSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        // 1. Academic Dates Section Card
                        val academicSummary = "${userState.profile?.schoolStartDate?.ifBlank { "2026-09-12" } ?: startDate} → ${userState.profile?.schoolEndDate?.ifBlank { "2027-06-09" } ?: endDate} • ${if (saturdayFree) strings.saturdayFreeLabel else strings.saturdayClassesDesc}"
                        SectionMenuItem(
                            icon = Icons.Default.CalendarMonth,
                            iconTint = MaterialTheme.colorScheme.primary,
                            iconBg = MaterialTheme.colorScheme.primaryContainer,
                            title = strings.academicDatesSection,
                            subtitle = academicSummary,
                            onClick = { selectedSection = SettingsSection.ACADEMIC_DATES },
                            testTag = "settings_section_academic_card"
                        )

                        // 2. Language Section Card
                        SectionMenuItem(
                            icon = Icons.Default.Translate,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            iconBg = MaterialTheme.colorScheme.secondaryContainer,
                            title = strings.languageSection,
                            subtitle = languageManager.currentLanguage.label,
                            onClick = { selectedSection = SettingsSection.LANGUAGE },
                            testTag = "settings_section_language_card"
                        )

                        // 3. Account Section Card
                        val accountEmail = authState.verificationEmail.ifBlank { userState.profile?.email ?: strings.registeredStudent }
                        SectionMenuItem(
                            icon = Icons.Default.AccountCircle,
                            iconTint = MaterialTheme.colorScheme.tertiary,
                            iconBg = MaterialTheme.colorScheme.tertiaryContainer,
                            title = strings.accountSection,
                            subtitle = accountEmail,
                            onClick = { selectedSection = SettingsSection.ACCOUNT },
                            testTag = "settings_section_account_card"
                        )

                        // 4. About App Section Card
                        SectionMenuItem(
                            icon = Icons.Default.Info,
                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconBg = MaterialTheme.colorScheme.surfaceVariant,
                            title = strings.aboutAppSection,
                            subtitle = "${strings.appName} • ${strings.versionLabel}",
                            onClick = { selectedSection = SettingsSection.ABOUT },
                            testTag = "settings_section_about_card"
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // ==================== SECTION DETAIL VIEW ====================
                        // Category Pills to quickly switch or see active section
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SettingsSection.values()) { section ->
                        val isSelected = selectedSection == section
                        val label = when (section) {
                            SettingsSection.ACADEMIC_DATES -> strings.academicDatesSection
                            SettingsSection.LANGUAGE -> strings.languageSection
                            SettingsSection.ACCOUNT -> strings.accountSection
                            SettingsSection.ABOUT -> strings.aboutAppSection
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSection = section },
                            label = { Text(label, fontSize = 13.sp) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                when (currentSection) {
                    SettingsSection.ACADEMIC_DATES -> {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = strings.academicDatesSection,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = strings.settingsDatesDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                DatePickerField(
                                    label = strings.schoolStartLabel,
                                    selectedDate = startDate,
                                    onDateSelected = {
                                        startDate = it
                                        validationError = null
                                    },
                                    testTag = "settings_start_date_field"
                                )

                                DatePickerField(
                                    label = strings.schoolEndLabel,
                                    selectedDate = endDate,
                                    onDateSelected = {
                                        endDate = it
                                        validationError = null
                                    },
                                    testTag = "settings_end_date_field"
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = strings.saturdayFreeLabel,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (saturdayFree) strings.saturdayFreeDesc else strings.saturdayClassesDesc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Switch(
                                        checked = saturdayFree,
                                        onCheckedChange = { saturdayFree = it },
                                        modifier = Modifier.testTag("settings_saturday_free_switch")
                                    )
                                }

                                Button(
                                    onClick = {
                                        val start = SchoolCalendarUtils.parseDate(startDate)
                                        val end = SchoolCalendarUtils.parseDate(endDate)
                                        if (start == null || end == null) {
                                            validationError = strings.selectBothDates
                                        } else if (!end.isAfter(start)) {
                                            validationError = strings.dateValidationError
                                        } else {
                                            validationError = null
                                            userViewModel.saveSchoolDates(
                                                startDate = startDate,
                                                endDate = endDate,
                                                saturdayFree = saturdayFree,
                                                strings = strings,
                                                languageCode = languageManager.currentLanguage.code
                                            ) {}
                                        }
                                    },
                                    enabled = !userState.isLoading,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("update_school_dates_btn")
                                ) {
                                    if (userState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = strings.updateDatesBtn,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SettingsSection.LANGUAGE -> {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = strings.languageSection,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                AppLanguage.values().forEach { langOption ->
                                    Surface(
                                        onClick = {
                                            languageManager.setLanguage(langOption, syncToCloud = true)
                                            userViewModel.updateLanguage(langOption.code)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (languageManager.currentLanguage == langOption)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("lang_option_${langOption.code}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = langOption.label,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (languageManager.currentLanguage == langOption) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (languageManager.currentLanguage == langOption)
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                else
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                            if (languageManager.currentLanguage == langOption) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SettingsSection.ACCOUNT -> {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = strings.accountSection,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = strings.connectedEmail,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = authState.verificationEmail.ifBlank { userState.profile?.email ?: strings.registeredStudent },
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { showSignOutConfirm = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("settings_sign_out_btn")
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.signOutBtn,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    SettingsSection.ABOUT -> {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = strings.appName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = strings.versionLabel,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = strings.aboutAppDesc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = strings.openSourceNotice,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            title = { Text(strings.signOutBtn) },
            text = { Text(strings.signOutConfirmText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutConfirm = false
                        authViewModel.signOut()
                    },
                    modifier = Modifier.testTag("confirm_sign_out_btn")
                ) {
                    Text(strings.signOutBtn, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun SectionMenuItem(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    iconBg: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
