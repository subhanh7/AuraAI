package com.example.auraai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val phone: String = "",
    val selectedTraits: List<String> = emptyList(),
    val isOnboardingCompleted: Boolean = false
)
