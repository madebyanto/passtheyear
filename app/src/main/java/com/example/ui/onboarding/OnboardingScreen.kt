package com.example.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTopBar
import com.example.ui.components.DatePickerField
import com.example.ui.components.InfoBanner
import com.example.ui.user.UserViewModel
import com.example.util.LocalAppLanguage
import com.example.util.SchoolCalendarUtils
import com.example.util.getTranslations
import java.time.LocalDate

@Composable
fun OnboardingScreen(
    userViewModel: UserViewModel,
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by userViewModel.uiState.collectAsState()
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)

    // Sensible defaults: Mid-September of current academic year to early June
    val currentYear = LocalDate.now().year
    val defaultStart = if (LocalDate.now().monthValue >= 9) "$currentYear-09-12" else "${currentYear - 1}-09-12"
    val defaultEnd = if (LocalDate.now().monthValue >= 9) "${currentYear + 1}-06-09" else "$currentYear-06-09"

    var startDate by remember { mutableStateOf(defaultStart) }
    var endDate by remember { mutableStateOf(defaultEnd) }
    var saturdayFree by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Live preview calculation including saturdayFree
    val metrics = remember(startDate, endDate, saturdayFree) {
        if (startDate.isNotBlank() && endDate.isNotBlank()) {
            SchoolCalendarUtils.calculateMetrics(
                startDateStr = startDate,
                endDateStr = endDate,
                saturdayFree = saturdayFree
            )
        } else null
    }

    Scaffold(
        topBar = {
            AppTopBar(title = strings.onboardingTitle)
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = strings.onboardingTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strings.onboardingSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (validationError != null) {
                InfoBanner(
                    message = validationError!!,
                    isError = true,
                    onDismiss = { validationError = null }
                )
            }

            if (uiState.error != null) {
                InfoBanner(
                    message = uiState.error!!,
                    isError = true,
                    onDismiss = { userViewModel.clearMessages() }
                )
            }

            // Start Date Picker
            DatePickerField(
                label = strings.schoolStartLabel,
                selectedDate = startDate,
                onDateSelected = {
                    startDate = it
                    validationError = null
                },
                testTag = "onboarding_start_date_field"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // End Date Picker
            DatePickerField(
                label = strings.schoolEndLabel,
                selectedDate = endDate,
                onDateSelected = {
                    endDate = it
                    validationError = null
                },
                testTag = "onboarding_end_date_field"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Saturday Off Switch Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.saturdayFreeLabel,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
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
                        modifier = Modifier.testTag("saturday_free_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preview Card
            if (metrics != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.calendarPreview,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = strings.totalDays,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${metrics.daysRemaining} ${strings.remaining}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Column {
                                Text(
                                    text = strings.estimatedClassDays,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "~${metrics.actualSchoolDaysRemaining} ${strings.daysLabel}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

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
                        userViewModel.saveSchoolDates(startDate, endDate, saturdayFree, strings) {
                            onOnboardingFinished()
                        }
                    }
                },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_submit_btn")
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = strings.startJourneyBtn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
