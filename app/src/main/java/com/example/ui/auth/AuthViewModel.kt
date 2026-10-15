package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.util.TranslationSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val infoMessage: String? = null,
    val isVerificationRequired: Boolean = false,
    val verificationEmail: String = "",
    val isAuthenticated: Boolean = false,
    val currentUserId: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        val user = authRepository.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        currentUserId = user.uid,
                        verificationEmail = user.email ?: ""
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isVerificationRequired = true,
                        verificationEmail = user.email ?: "",
                        currentUserId = user.uid
                    )
                }
            }
        }
    }

    fun login(email: String, pass: String, strings: TranslationSet) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = strings.fillAllFields) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, infoMessage = null) }
            when (val result = authRepository.signInWithEmail(email, pass)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            isVerificationRequired = false,
                            currentUserId = result.user.uid,
                            verificationEmail = result.user.email ?: email
                        )
                    }
                }
                is AuthResult.VerificationRequired -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            isVerificationRequired = true,
                            verificationEmail = result.user.email ?: email,
                            currentUserId = result.user.uid,
                            infoMessage = strings.emailNotVerifiedYet
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun signUp(email: String, pass: String, confirmPass: String, strings: TranslationSet) {
        if (email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.update { it.copy(error = strings.fillAllFields) }
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(error = strings.passwordTooShortError) }
            return
        }
        if (pass != confirmPass) {
            _uiState.update { it.copy(error = strings.passwordMismatchError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, infoMessage = null) }
            when (val result = authRepository.signUpWithEmail(email, pass)) {
                is AuthResult.VerificationRequired -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isVerificationRequired = true,
                            verificationEmail = result.user.email ?: email,
                            currentUserId = result.user.uid,
                            infoMessage = strings.accountCreatedCheckEmail
                        )
                    }
                }
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            currentUserId = result.user.uid
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun checkEmailVerification(strings: TranslationSet) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val isVerified = authRepository.checkEmailVerified()
            if (isVerified) {
                val user = authRepository.currentUser
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isVerificationRequired = false,
                        isAuthenticated = true,
                        currentUserId = user?.uid,
                        infoMessage = strings.emailVerifiedSuccess
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = strings.emailNotVerifiedYet
                    )
                }
            }
        }
    }

    fun resendVerificationEmail(strings: TranslationSet) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = authRepository.resendVerificationEmail()
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        infoMessage = strings.verificationEmailResent
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = res.exceptionOrNull()?.localizedMessage ?: strings.errorGeneric
                    )
                }
            }
        }
    }

    fun sendPasswordReset(email: String, strings: TranslationSet, onSuccess: () -> Unit) {
        if (email.isBlank()) {
            _uiState.update { it.copy(error = strings.invalidEmailError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = authRepository.sendPasswordReset(email)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        infoMessage = strings.resetEmailSent
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = res.exceptionOrNull()?.localizedMessage ?: strings.errorGeneric
                    )
                }
            }
        }
    }

    fun dismissVerificationScreen() {
        authRepository.signOut()
        _uiState.update {
            AuthUiState()
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.update {
            AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, infoMessage = null) }
    }
}
