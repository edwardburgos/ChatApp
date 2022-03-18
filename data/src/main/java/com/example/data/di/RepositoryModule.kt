package com.example.data.di

import com.example.data.repository.MessagesRepository
import com.example.data.repository.MessagesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun providesMessagesRepository(messagesRepositoryImpl: MessagesRepositoryImpl): MessagesRepository {
        return messagesRepositoryImpl
    }
}