package com.example.data.repository

import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date

sealed class AuthResult {
    data class Success(val user: FirebaseUser, val isEmailVerified: Boolean) : AuthResult()
    data class VerificationRequired(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isEmailVerified: Boolean
        get() = auth.currentUser?.isEmailVerified == true

    suspend fun signUpWithEmail(email: String, pass: String): AuthResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val user = authResult.user ?: return AuthResult.Error("Creazione utente fallita")

            // Send mandatory verification email as required
            try {
                user.sendEmailVerification().await()
            } catch (e: Exception) {
                // Keep moving even if email send has minor error, will allow resend
            }

            // Create initial user document in Firestore: users/{UID}
            val initialProfile = UserProfile(
                userId = user.uid,
                email = user.email ?: email.trim(),
                schoolStartDate = "",
                schoolEndDate = "",
                saturdayFree = true,
                onboardingCompleted = false,
                creationDate = com.google.firebase.Timestamp.now(),
                updatedAt = com.google.firebase.Timestamp.now()
            )

            try {
                firestore.collection("users").document(user.uid)
                    .set(initialProfile, SetOptions.merge()).await()
            } catch (e: Exception) {
                // If firestore write fails, log but proceed
            }

            AuthResult.VerificationRequired(user)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Errore durante la registrazione")
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): AuthResult {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val user = authResult.user ?: return AuthResult.Error("Utente non trovato")

            // Check if email has been verified
            if (user.isEmailVerified) {
                AuthResult.Success(user, true)
            } else {
                AuthResult.VerificationRequired(user)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Credenziali non valide o errore di accesso")
        }
    }

    suspend fun checkEmailVerified(): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            user.reload().await()
            user.isEmailVerified
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resendVerificationEmail(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Nessun utente autenticato"))
        return try {
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
