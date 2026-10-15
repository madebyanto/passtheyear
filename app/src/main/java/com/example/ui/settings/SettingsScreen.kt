package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.auth.AuthViewModel
import com.example.ui.components.AppTopBar
import com.example.ui.components.DatePickerField
import com.example.ui.components.InfoBanner
import com.example.ui.user.UserViewModel
import com.example.util.AppLanguage
import com.example.util.LocalAppLanguage
import com.example.util.SchoolCalendarUtils
import com.example.util.getTranslations

@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val userState by userViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val languageManager = LocalAppLanguage.current
    val strings = getTranslations(languageManager.currentLanguage)

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

    Scaffold(
        topBar = {
            AppTopBar(title = strings.settingsTitle)
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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

            // 1. ACADEMIC DATES SECTION
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
                                userViewModel.saveSchoolDates(startDate, endDate, saturdayFree, strings) {}
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

            // 2. LANGUAGE SECTION
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
                            onClick = { languageManager.setLanguage(langOption) },
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
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
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

            // 3. ACCOUNT & SESSION SECTION
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

                    Column {
                        Text(
                            text = strings.connectedEmail,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = authState.verificationEmail.ifBlank { userState.profile?.email ?: strings.registeredStudent },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                        Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.signOutBtn,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
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
