package com.example.auraai.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.example.auraai.data.audio.AudioRecorder
import com.example.auraai.data.audio.AudioRecorderImpl
import com.example.auraai.data.local.datastore.UserProfileSerializer
import com.example.auraai.data.local.room.AppDatabase
import com.example.auraai.data.local.room.dao.ChatMessageDao
import com.example.auraai.data.local.room.dao.ReminderDao
import com.example.auraai.data.local.room.dao.UserProfileDao
import com.example.auraai.data.repository.ChatRepositoryImpl
import com.example.auraai.data.repository.ReminderRepositoryImpl
import com.example.auraai.data.repository.UserRepositoryImpl
import com.example.auraai.domain.model.UserProfile
import com.example.auraai.domain.repository.ChatRepository
import com.example.auraai.domain.repository.ReminderRepository
import com.example.auraai.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideUserProfileDataStore(@ApplicationContext context: Context): DataStore<UserProfile> {
        return DataStoreFactory.create(
            serializer = UserProfileSerializer,
            produceFile = { context.dataStoreFile("user_profile.json") }
        )
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "aura_ai_db"
        ).build()
    }

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()

    @Provides
    fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()

    @Provides
    @Singleton
    fun provideUserRepository(
        dataStore: DataStore<UserProfile>,
        userProfileDao: UserProfileDao
    ): UserRepository {
        return UserRepositoryImpl(dataStore, userProfileDao)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatMessageDao: ChatMessageDao): ChatRepository {
        return ChatRepositoryImpl(chatMessageDao)
    }

    @Provides
    @Singleton
    fun provideReminderRepository(reminderDao: ReminderDao): ReminderRepository {
        return ReminderRepositoryImpl(reminderDao)
    }

    @Provides
    @Singleton
    fun provideAudioRecorder(): AudioRecorder {
        return AudioRecorderImpl()
    }
}
