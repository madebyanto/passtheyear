package com.example.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.UserRepository
import com.example.util.SchoolCalendarUtils
import com.example.util.SchoolYearMetrics
import com.example.util.TranslationSet
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val metrics: SchoolYearMetrics? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class UserViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private var activeUserId: String? = null
    private var observeJob: kotlinx.coroutines.Job? = null

    fun reset() {
        observeJob?.cancel()
        observeJob = null
        activeUserId = null
        _uiState.value = UserUiState()
    }

    fun bindUser(userId: String) {
        if (activeUserId == userId && observeJob?.isActive == true) return
        activeUserId = userId
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.getUserProfile(userId).collect { profile ->
                val metrics = if (profile != null && profile.schoolStartDate.isNotBlank() && profile.schoolEndDate.isNotBlank()) {
                    SchoolCalendarUtils.calculateMetrics(
                        startDateStr = profile.schoolStartDate,
                        endDateStr = profile.schoolEndDate,
                        saturdayFree = profile.saturdayFree
                    )
                } else null

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = profile,
                        metrics = metrics
                    )
                }
            }
        }
    }

    fun saveSchoolDates(
        startDate: String,
        endDate: String,
        saturdayFree: Boolean,
        strings: TranslationSet,
        languageCode: String = "en",
        onSuccess: () -> Unit
    ) {
        val uid = activeUserId ?: FirebaseAuth.getInstance().currentUser?.uid ?: return
        val email = FirebaseAuth.getInstance().currentUser?.email ?: (_uiState.value.profile?.email ?: "")

        // 1. Optimistically update local state immediately so Onboarding doesn't bounce back
        val optimisticProfile = UserProfile(
            userId = uid,
            email = email,
            schoolStartDate = startDate,
            schoolEndDate = endDate,
            saturdayFree = saturdayFree,
            onboardingCompleted = true,
            language = languageCode
        )
        val metrics = SchoolCalendarUtils.calculateMetrics(
            startDateStr = startDate,
            endDateStr = endDate,
            saturdayFree = saturdayFree
        )

        _uiState.update {
            it.copy(
                profile = optimisticProfile,
                metrics = metrics,
                isLoading = false,
                error = null,
                successMessage = strings.datesUpdatedSuccess
            )
        }

        // Trigger immediate navigation transition
        onSuccess()

        // 2. Persist to Firestore asynchronously
        viewModelScope.launch {
            val res = userRepository.saveSchoolDates(uid, startDate, endDate, saturdayFree, languageCode)
            if (res.isFailure) {
                _uiState.update {
                    it.copy(
                        error = res.exceptionOrNull()?.localizedMessage ?: strings.errorGeneric
                    )
                }
            }
        }
    }

    fun updateLanguage(languageCode: String) {
        val uid = activeUserId ?: FirebaseAuth.getInstance().currentUser?.uid

        // Optimistically update local profile
        _uiState.update { state ->
            val currentProfile = state.profile
            if (currentProfile != null) {
                state.copy(profile = currentProfile.copy(language = languageCode))
            } else {
                state
            }
        }

        if (uid != null) {
            viewModelScope.launch {
                userRepository.updateLanguage(uid, languageCode)
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
