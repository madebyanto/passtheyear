package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppLanguage
import com.example.util.LocalAppLanguage
import com.example.util.getTranslations
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    showLanguageSelector: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val languageManager = LocalAppLanguage.current
    val strings = getTranslations(languageManager.currentLanguage)
    var showLanguageMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("top_bar_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.back
                    )
                }
            }
        },
        actions = {
            if (showLanguageSelector) {
                Box {
                    IconButton(
                        onClick = { showLanguageMenu = true },
                        modifier = Modifier.testTag("lang_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = strings.languageSection
                        )
                    }
                    DropdownMenu(
                        expanded = showLanguageMenu,
                        onDismissRequest = { showLanguageMenu = false }
                    ) {
                        AppLanguage.values().forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang.label) },
                                onClick = {
                                    languageManager.setLanguage(lang)
                                    showLanguageMenu = false
                                },
                                trailingIcon = {
                                    if (languageManager.currentLanguage == lang) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "date_picker_field"
) {
    val context = LocalContext.current
    val strings = getTranslations(LocalAppLanguage.current.currentLanguage)
    val currentOnDateSelected by rememberUpdatedState(onDateSelected)

    val showDatePicker = {
        val cal = Calendar.getInstance()
        if (selectedDate.isNotBlank()) {
            try {
                val parts = selectedDate.split("-")
                if (parts.size == 3) {
                    cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                }
            } catch (_: Exception) {}
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                currentOnDateSelected(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    OutlinedCard(
        onClick = showDatePicker,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (selectedDate.isNotBlank()) selectedDate else strings.tapToSelect,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = if (selectedDate.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
            }
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun InfoBanner(
    message: String,
    isError: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val strings = getTranslations(LocalAppLanguage.current.currentLanguage)
    AnimatedVisibility(
        visible = message.isNotBlank(),
        enter = fadeIn(tween(220)) + expandVertically(tween(260)),
        exit = fadeOut(tween(180)) + shrinkVertically(tween(220))
    ) {
        val containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
        val contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
        val icon = if (isError) Icons.Default.Warning else Icons.Default.CheckCircle

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MoodBadge(
    mood: String,
    modifier: Modifier = Modifier
) {
    val strings = getTranslations(LocalAppLanguage.current.currentLanguage)
    val (bgColor, emoji, localizedLabel) = when (mood.lowercase()) {
        "great", "ottimo", "genial" -> Triple(Color(0xFFE8F5E9), "🌟", strings.moodGreat)
        "calm", "serene", "sereno", "tranquilo" -> Triple(Color(0xFFE0F2F1), "😊", strings.moodSerene)
        "neutral", "neutro" -> Triple(Color(0xFFEDE7F6), "😐", strings.moodNeutral)
        "anxious", "ansioso" -> Triple(Color(0xFFFFF3E0), "😰", strings.moodAnxious)
        "stressed", "stressato", "estresado" -> Triple(Color(0xFFFFEBEE), "🤯", strings.moodStressed)
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, "📝", mood)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = "$emoji $localizedLabel",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
