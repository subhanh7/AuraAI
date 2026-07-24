package com.example.auraai.domain.repository

import com.example.auraai.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userProfile: Flow<UserProfile>
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun updateOnboardingStatus(completed: Boolean)
}
