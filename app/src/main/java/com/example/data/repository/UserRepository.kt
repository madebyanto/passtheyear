package com.example.data.repository

import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getUserProfile(userId: String): Flow<UserProfile?> {
        return firestore.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    try {
                        snapshot.toObject(UserProfile::class.java)?.copy(userId = snapshot.id)
                    } catch (_: Exception) {
                        val email = snapshot.getString("email") ?: ""
                        val startDate = snapshot.getString("schoolStartDate") ?: ""
                        val endDate = snapshot.getString("schoolEndDate") ?: ""
                        val satFree = snapshot.getBoolean("saturdayFree") ?: true
                        val onboarding = snapshot.getBoolean("onboardingCompleted") ?: (startDate.isNotBlank() && endDate.isNotBlank())
                        val lang = snapshot.getString("language") ?: "en"
                        val creation = snapshot.getTimestamp("creationDate")
                        val updated = snapshot.getTimestamp("updatedAt")
                        UserProfile(
                            userId = snapshot.id,
                            email = email,
                            schoolStartDate = startDate,
                            schoolEndDate = endDate,
                            saturdayFree = satFree,
                            onboardingCompleted = onboarding,
                            language = lang,
                            creationDate = creation,
                            updatedAt = updated
                        )
                    }
                } else {
                    null
                }
            }
            .catch { emit(null) }
    }

    suspend fun saveSchoolDates(
        userId: String,
        startDate: String,
        endDate: String,
        saturdayFree: Boolean,
        languageCode: String = "en"
    ): Result<Unit> {
        return try {
            val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val updates = mutableMapOf<String, Any>(
                "userId" to userId,
                "email" to email,
                "schoolStartDate" to startDate,
                "schoolEndDate" to endDate,
                "saturdayFree" to saturdayFree,
                "onboardingCompleted" to true,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (languageCode.isNotBlank()) {
                updates["language"] = languageCode
            }
            firestore.collection("users").document(userId)
                .set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLanguage(
        userId: String,
        languageCode: String
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "language" to languageCode,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(userId)
                .set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSchoolDates(
        userId: String,
        startDate: String,
        endDate: String,
        saturdayFree: Boolean,
        languageCode: String = "en"
    ): Result<Unit> {
        return saveSchoolDates(userId, startDate, endDate, saturdayFree, languageCode)
    }
}
