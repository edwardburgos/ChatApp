package com.example.data.di

import android.content.Context
import com.example.data.database.MessagesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun providesMessagesDatabase(@ApplicationContext appContext: Context): MessagesDatabase {
        return MessagesDatabase.getInstance(appContext)
    }
}