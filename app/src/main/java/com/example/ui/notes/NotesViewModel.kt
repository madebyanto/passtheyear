package com.example.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotesUiState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val selectedCategory: String = "Tutte",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class NotesViewModel(
    private val noteRepository: NoteRepository = NoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var activeUserId: String? = null

    fun bindUser(userId: String) {
        if (activeUserId == userId) return
        activeUserId = userId

        viewModelScope.launch {
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

    fun addNote(title: String, content: String, category: String, mood: String, onSuccess: () -> Unit) {
        val uid = activeUserId ?: return
        if (title.isBlank() && content.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Inserisci almeno un titolo o del contenuto") }
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
                        successMessage = "Nota salvata!"
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Errore aggiunta nota"
                    )
                }
            }
        }
    }

    fun updateNote(note: Note, onSuccess: () -> Unit) {
        val uid = activeUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val res = noteRepository.updateNote(uid, note)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Nota aggiornata!"
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Errore modifica nota"
                    )
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        val uid = activeUserId ?: return
        viewModelScope.launch {
            noteRepository.deleteNote(uid, noteId)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
