package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val userId: String = "",
    val email: String = "",
    val schoolStartDate: String = "", // e.g. "2026-09-12"
    val schoolEndDate: String = "",   // e.g. "2027-06-09"
    val saturdayFree: Boolean = true, // true = 5 days/week (Mon-Fri), false = 6 days/week (Mon-Sat)
    val onboardingCompleted: Boolean = false,
    val language: String = "en",      // "en", "it", "es"
    @ServerTimestamp val creationDate: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
