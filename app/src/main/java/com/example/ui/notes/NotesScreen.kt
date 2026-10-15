package com.example.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.ui.components.AppTopBar
import com.example.ui.components.InfoBanner
import com.example.ui.components.MoodBadge
import com.example.util.LocalAppLanguage
import com.example.util.TranslationSet
import com.example.util.getTranslations
import java.text.SimpleDateFormat
import java.util.Locale

data class CategoryItem(val key: String, val label: (TranslationSet) -> String)
data class MoodItem(val key: String, val label: (TranslationSet) -> String, val emoji: String)

val CategoryOptions = listOf(
    CategoryItem("all") { it.categoryAll },
    CategoryItem("study") { it.categoryStudy },
    CategoryItem("anxiety") { it.categoryAnxiety },
    CategoryItem("reminder") { it.categoryReminder },
    CategoryItem("ideas") { it.categoryIdeas },
    CategoryItem("wellness") { it.categoryWellness }
)

val MoodOptions = listOf(
    MoodItem("great", { it.moodGreat }, "🌟"),
    MoodItem("calm", { it.moodSerene }, "😊"),
    MoodItem("neutral", { it.moodNeutral }, "😐"),
    MoodItem("anxious", { it.moodAnxious }, "😰"),
    MoodItem("stressed", { it.moodStressed }, "🤯")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notesViewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by notesViewModel.uiState.collectAsState()
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)

    var showAddEditDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    val filteredNotes = remember(uiState.notes, uiState.selectedCategory) {
        if (uiState.selectedCategory == "all" || uiState.selectedCategory == "Tutte") {
            uiState.notes
        } else {
            uiState.notes.filter {
                it.category.equals(uiState.selectedCategory, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = strings.notesTitle)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    noteToEdit = null
                    showAddEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("add_note_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.addNoteBtn
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CategoryOptions) { catItem ->
                    val isSelected = (uiState.selectedCategory == catItem.key) ||
                            (uiState.selectedCategory == "Tutte" && catItem.key == "all")
                    FilterChip(
                        selected = isSelected,
                        onClick = { notesViewModel.selectCategory(catItem.key) },
                        label = { Text(catItem.label(strings)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_chip_${catItem.key}")
                    )
                }
            }

            if (uiState.errorMessage != null) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    InfoBanner(
                        message = uiState.errorMessage!!,
                        isError = true,
                        onDismiss = { notesViewModel.clearMessages() }
                    )
                }
            }

            if (filteredNotes.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NoteAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = strings.emptyNotesText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onEdit = {
                                noteToEdit = note
                                showAddEditDialog = true
                            },
                            onDelete = {
                                noteToDelete = note
                            }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditNoteDialog(
            note = noteToEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { title, content, cat, mood ->
                if (noteToEdit != null) {
                    notesViewModel.updateNote(
                        noteToEdit!!.copy(
                            title = title,
                            content = content,
                            category = cat,
                            mood = mood
                        )
                    ) {
                        showAddEditDialog = false
                    }
                } else {
                    notesViewModel.addNote(title, content, cat, mood) {
                        showAddEditDialog = false
                    }
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text(strings.delete) },
            text = { Text(strings.deleteNoteConfirm) },
            confirmButton = {
                TextButton(
                    onClick = {
                        notesViewModel.deleteNote(noteToDelete!!.id)
                        noteToDelete = null
                    },
                    modifier = Modifier.testTag("confirm_delete_note_btn")
                ) {
                    Text(strings.delete, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
fun NoteCard(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)
    val dateFormat = remember(lang) {
        SimpleDateFormat("dd MMM yyyy, HH:mm", when (lang) {
            com.example.util.AppLanguage.ENGLISH -> Locale.ENGLISH
            com.example.util.AppLanguage.SPANISH -> Locale("es", "ES")
            com.example.util.AppLanguage.ITALIAN -> Locale.ITALIAN
        })
    }
    val formattedDate = note.createdAt?.let { dateFormat.format(it) } ?: strings.recent

    // Localized category display
    val localizedCategory = when (note.category.lowercase()) {
        "study", "studio", "estudio" -> strings.categoryStudy
        "anxiety", "ansia", "ansiedad" -> strings.categoryAnxiety
        "reminder", "promemoria", "recordatorio" -> strings.categoryReminder
        "ideas", "idee" -> strings.categoryIdeas
        "wellness", "benessere", "bienestar" -> strings.categoryWellness
        else -> note.category
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("note_card_${note.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = localizedCategory,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    MoodBadge(mood = note.mood)
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_note_btn_${note.id}")) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = strings.edit,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_note_btn_${note.id}")) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = strings.delete,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteDialog(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: String, mood: String) -> Unit
) {
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)

    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var selectedCategoryKey by remember {
        mutableStateOf(note?.category?.lowercase() ?: "study")
    }
    var selectedMoodKey by remember {
        mutableStateOf(note?.mood?.lowercase() ?: "calm")
    }

    val selectableCategories = CategoryOptions.filter { it.key != "all" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (note == null) strings.addNoteBtn else strings.editNoteBtn,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.noteTitleLabel) },
                    placeholder = { Text(strings.noteTitlePlaceholder) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("note_input_title")
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(strings.noteContentLabel) },
                    placeholder = { Text(strings.noteContentPlaceholder) },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("note_input_content")
                )

                Text(
                    text = strings.noteCategoryLabel,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(selectableCategories) { catItem ->
                        FilterChip(
                            selected = selectedCategoryKey == catItem.key,
                            onClick = { selectedCategoryKey = catItem.key },
                            label = { Text(catItem.label(strings), fontSize = 12.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Text(
                    text = strings.noteMoodLabel,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MoodOptions) { moodItem ->
                        FilterChip(
                            selected = selectedMoodKey == moodItem.key,
                            onClick = { selectedMoodKey = moodItem.key },
                            label = { Text("${moodItem.emoji} ${moodItem.label(strings)}", fontSize = 12.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, content, selectedCategoryKey, selectedMoodKey) },
                enabled = title.isNotBlank() || content.isNotBlank(),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_note_confirm_btn")
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
