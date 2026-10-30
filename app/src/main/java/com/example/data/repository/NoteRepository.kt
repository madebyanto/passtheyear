package com.example.data.repository

import com.example.data.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date

class NoteRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun observeNotes(userId: String): Flow<List<Note>> {
        if (userId.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        return firestore.collection("users").document(userId).collection("notes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Note::class.java)?.copy(id = doc.id, userId = userId)
                    } catch (_: Exception) {
                        try {
                            val title = doc.getString("title") ?: ""
                            val content = doc.getString("content") ?: ""
                            val category = doc.getString("category") ?: "study"
                            val mood = doc.getString("mood") ?: "calm"
                            val createdAt = doc.getTimestamp("createdAt")
                            val updatedAt = doc.getTimestamp("updatedAt")
                            Note(
                                id = doc.id,
                                userId = userId,
                                title = title,
                                content = content,
                                category = category,
                                mood = mood,
                                createdAt = createdAt,
                                updatedAt = updatedAt
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
            }
            .catch {
                // If orderBy fails due to missing index on new collection or offline state, fallback to basic list
                try {
                    val fallback = firestore.collection("users").document(userId).collection("notes").get().await()
                    emit(fallback.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Note::class.java)?.copy(id = doc.id, userId = userId)
                        } catch (_: Exception) {
                            null
                        }
                    })
                } catch (_: Exception) {
                    emit(emptyList())
                }
            }
    }

    suspend fun addNote(userId: String, note: Note): Result<String> {
        return try {
            val docRef = firestore.collection("users").document(userId).collection("notes").document()
            val noteWithId = note.copy(
                id = docRef.id,
                userId = userId,
                createdAt = com.google.firebase.Timestamp.now(),
                updatedAt = com.google.firebase.Timestamp.now()
            )
            docRef.set(noteWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNote(userId: String, note: Note): Result<Unit> {
        return try {
            val noteId = note.id
            require(noteId.isNotBlank()) { "Note ID cannot be blank for update" }
            val noteToUpdate = note.copy(updatedAt = com.google.firebase.Timestamp.now())
            firestore.collection("users").document(userId).collection("notes").document(noteId)
                .set(noteToUpdate).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNote(userId: String, noteId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("notes").document(noteId)
                .delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
