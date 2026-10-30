package com.example.ui.wellness

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurvivalTip
import com.example.data.model.SurvivalTipsData
import com.example.ui.components.AppTopBar
import com.example.util.LocalAppLanguage
import com.example.util.getTranslations
import kotlinx.coroutines.delay

enum class BreathingPhase {
    INHALE,
    HOLD_IN,
    EXHALE,
    HOLD_OUT
}

@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)
    val tips = SurvivalTipsData.tips

    Scaffold(
        topBar = {
            AppTopBar(title = strings.wellnessTitle)
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
                    text = strings.wellnessSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Interactive Box Breathing Widget
            item {
                BoxBreathingWidget()
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.guidesAndTechniques,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            items(tips, key = { it.id }) { tip ->
                SurvivalTipCard(tip = tip)
            }
        }
    }
}

@Composable
fun BoxBreathingWidget() {
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)

    var isRunning by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var secondsLeft by remember { mutableIntStateOf(4) }

    // Breathing loop timer
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                delay(1000L)
                if (secondsLeft > 1) {
                    secondsLeft -= 1
                } else {
                    secondsLeft = 4
                    currentPhase = when (currentPhase) {
                        BreathingPhase.INHALE -> BreathingPhase.HOLD_IN
                        BreathingPhase.HOLD_IN -> BreathingPhase.EXHALE
                        BreathingPhase.EXHALE -> BreathingPhase.HOLD_OUT
                        BreathingPhase.HOLD_OUT -> BreathingPhase.INHALE
                    }
                }
            }
        } else {
            currentPhase = BreathingPhase.INHALE
            secondsLeft = 4
        }
    }

    // Smooth breathing circle animation
    val targetScale = when {
        !isRunning -> 1f
        currentPhase == BreathingPhase.INHALE -> 1.35f
        currentPhase == BreathingPhase.HOLD_IN -> 1.35f
        currentPhase == BreathingPhase.EXHALE -> 0.9f
        else -> 0.9f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = if (isRunning && (currentPhase == BreathingPhase.INHALE || currentPhase == BreathingPhase.EXHALE)) 4000 else 400),
        label = "breathing_circle_scale"
    )

    val currentPhaseLabel = when (currentPhase) {
        BreathingPhase.INHALE -> strings.inhale
        BreathingPhase.HOLD_IN -> strings.hold
        BreathingPhase.EXHALE -> strings.exhale
        BreathingPhase.HOLD_OUT -> strings.hold
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("box_breathing_widget")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = strings.boxBreathingTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.boxBreathingSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Breathing Circle
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Expanding outer ring
                Box(
                    modifier = Modifier
                        .size((110 * animatedScale).dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                )

                // Solid inner core
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isRunning) "$secondsLeft" else "4s",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = if (isRunning) currentPhaseLabel else strings.ready,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            Button(
                onClick = { isRunning = !isRunning },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("toggle_breathing_btn")
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRunning) strings.stopExercise else strings.startBreathing,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun SurvivalTipCard(
    tip: SurvivalTip,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("survival_tip_card_${tip.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tip.iconEmoji,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = tip.title(lang),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = tip.category(lang),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("expand_tip_${tip.id}")
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tip.content(lang),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(220)) + expandVertically(tween(260)),
                exit = fadeOut(tween(180)) + shrinkVertically(tween(220))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = strings.practicalSteps,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    tip.steps(lang).forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
