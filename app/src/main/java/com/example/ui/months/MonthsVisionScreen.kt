package com.example.ui.months

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthAtmosphere
import com.example.data.model.SchoolMonthsData
import com.example.ui.components.AppTopBar
import com.example.ui.user.UserViewModel
import com.example.util.LocalAppLanguage
import com.example.util.MonthProgressInfo
import com.example.util.MonthProgressStatus
import com.example.util.SchoolCalendarUtils
import com.example.util.getTranslations
import java.time.LocalDate

@Composable
fun MonthsVisionScreen(
    userViewModel: UserViewModel? = null,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)
    val currentMonthInt = LocalDate.now().monthValue
    val monthsList = SchoolMonthsData.months

    val userState = userViewModel?.uiState?.collectAsState()?.value
    val startDate = userState?.profile?.schoolStartDate
    val endDate = userState?.profile?.schoolEndDate

    Scaffold(
        topBar = {
            AppTopBar(title = strings.monthsVisionTitle)
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = strings.monthsVisionSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(monthsList, key = { it.monthIndex }) { monthData ->
                val progressInfo = remember(monthData.monthIndex, startDate, endDate) {
                    SchoolCalendarUtils.calculateMonthProgress(
                        monthIndex = monthData.monthIndex,
                        startDateStr = startDate,
                        endDateStr = endDate
                    )
                }

                MonthCard(
                    month = monthData,
                    isCurrentMonth = monthData.monthIndex == currentMonthInt,
                    progressInfo = progressInfo
                )
            }
        }
    }
}

@Composable
fun MonthCard(
    month: MonthAtmosphere,
    isCurrentMonth: Boolean,
    progressInfo: MonthProgressInfo,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)

    // Difficulty styling
    val diffColor = when {
        month.difficulty <= 4 -> Color(0xFF2E7D32) // Forest green
        month.difficulty <= 6 -> Color(0xFFE65100) // Vibrant amber
        month.difficulty <= 8 -> Color(0xFFD84315) // Strong orange/red
        else -> Color(0xFFC62828)                  // High alert red
    }

    val statusText = when (progressInfo.status) {
        MonthProgressStatus.COMPLETED -> strings.monthStatusCompleted
        MonthProgressStatus.IN_PROGRESS -> strings.monthStatusCurrent
        MonthProgressStatus.UPCOMING -> strings.monthStatusUpcoming
    }

    val animatedProgress by animateFloatAsState(
        targetValue = (progressInfo.progressPercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "month_card_progress"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentMonth)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isCurrentMonth)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentMonth) 4.dp else 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("month_card_${month.monthIndex}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Month Header with Name, Badge and Current indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${month.monthIndex}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (isCurrentMonth) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = month.localizedName(lang),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isCurrentMonth) {
                            Text(
                                text = strings.currentMonthBadge,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = month.localizedBadge(lang),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mood Title
            Text(
                text = month.localizedTitle(lang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Atmosphere Narrative
            Text(
                text = month.localizedAtmosphere(lang),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // STATS ROW: % Progress in Month & Difficulty 1-10
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // 1. Month Progress (%)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (progressInfo.status == MonthProgressStatus.COMPLETED)
                                    Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = if (progressInfo.status == MonthProgressStatus.COMPLETED)
                                    Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.monthProgressLabel,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${progressInfo.progressPercent}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (progressInfo.status == MonthProgressStatus.COMPLETED)
                                    Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (progressInfo.status) {
                                    MonthProgressStatus.COMPLETED -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                                    MonthProgressStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    MonthProgressStatus.UPCOMING -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                }
                            ) {
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = when (progressInfo.status) {
                                        MonthProgressStatus.COMPLETED -> Color(0xFF2E7D32)
                                        MonthProgressStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                                        MonthProgressStatus.UPCOMING -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (progressInfo.status == MonthProgressStatus.COMPLETED)
                            Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Difficulty (1/10)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = diffColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.difficultyLabel,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${month.difficulty}/10",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = diffColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = diffColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = month.localizedDifficultyDescription(lang),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = diffColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Visual meter for difficulty (1 to 10)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..10) {
                            val isFilled = i <= month.difficulty
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (isFilled) diffColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Survival Tip Box
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.survivalTipLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = month.localizedSurvivalTip(lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
