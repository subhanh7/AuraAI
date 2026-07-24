package com.example.auraai.data.repository

import androidx.datastore.core.DataStore
import com.example.auraai.data.local.room.dao.UserProfileDao
import com.example.auraai.data.local.room.entity.UserProfileEntity
import com.example.auraai.domain.model.UserProfile
import com.example.auraai.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<UserProfile>,
    private val userProfileDao: UserProfileDao
) : UserRepository {
    
    // Combining DataStore (for settings/onboarding) and Room (for profile details)
    override val userProfile: Flow<UserProfile> = combine(
        dataStore.data,
        userProfileDao.getUserProfile()
    ) { prefs, entity ->
        if (entity != null) {
            UserProfile(
                name = entity.name,
                age = entity.age,
                phone = entity.phone,
                selectedTraits = entity.traits.split(",").filter { it.isNotBlank() },
                isOnboardingCompleted = prefs.isOnboardingCompleted
            )
        } else {
            prefs
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        // Save to DataStore
        dataStore.updateData { it.copy(
            name = profile.name,
            age = profile.age,
            phone = profile.phone,
            selectedTraits = profile.selectedTraits,
            isOnboardingCompleted = profile.isOnboardingCompleted
        ) }
        
        // Save to Room for sync
        userProfileDao.insertUserProfile(
            UserProfileEntity(
                name = profile.name,
                age = profile.age,
                phone = profile.phone,
                traits = profile.selectedTraits.joinToString(",")
            )
        )
    }

    override suspend fun updateOnboardingStatus(completed: Boolean) {
        dataStore.updateData { it.copy(isOnboardingCompleted = completed) }
    }
}
