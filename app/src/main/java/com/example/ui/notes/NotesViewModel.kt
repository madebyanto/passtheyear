package com.example.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import com.example.util.TranslationSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotesUiState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val selectedCategory: String = "all",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class NotesViewModel(
    private val noteRepository: NoteRepository = NoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var activeUserId: String? = null
    private var observeJob: Job? = null

    fun reset() {
        observeJob?.cancel()
        observeJob = null
        activeUserId = null
        _uiState.value = NotesUiState()
    }

    fun bindUser(userId: String) {
        if (userId.isBlank()) return
        if (activeUserId == userId && observeJob?.isActive == true) return
        activeUserId = userId
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            noteRepository.observeNotes(userId).collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = list
                    )
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun addNote(
        title: String,
        content: String,
        category: String,
        mood: String,
        strings: TranslationSet? = null,
        onSuccess: () -> Unit
    ) {
        val uid = activeUserId ?: com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (title.isBlank() && content.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = strings?.noteEmptyError ?: "Please enter a title or note content")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val newNote = Note(
                userId = uid,
                title = title.trim(),
                content = content.trim(),
                category = category,
                mood = mood
            )
            val res = noteRepository.addNote(uid, newNote)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = strings?.noteSavedSuccess ?: "Note saved successfully!"
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = res.exceptionOrNull()?.localizedMessage ?: strings?.errorGeneric ?: "Error saving note"
                    )
                }
            }
        }
    }

    fun updateNote(
        note: Note,
        strings: TranslationSet? = null,
        onSuccess: () -> Unit
    ) {
        val uid = activeUserId ?: com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val res = noteRepository.updateNote(uid, note)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = strings?.noteUpdatedSuccess ?: "Note updated successfully!"
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = res.exceptionOrNull()?.localizedMessage ?: strings?.errorGeneric ?: "Error updating note"
                    )
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        val uid = activeUserId ?: com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            noteRepository.deleteNote(uid, noteId)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
