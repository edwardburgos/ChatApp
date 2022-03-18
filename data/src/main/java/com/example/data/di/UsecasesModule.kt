package com.example.data.di

import com.example.data.usecases.GetMessagesUseCase
import com.example.data.usecases.GetMessagesUseCaseImpl
import com.example.data.usecases.GetPagerUseCase
import com.example.data.usecases.GetPagerUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class UsecasesModule {
    @Provides
    fun providesGetMessagesUseCaseImpl(getMessagesUseCaseImpl: GetMessagesUseCaseImpl): GetMessagesUseCase {
        return getMessagesUseCaseImpl
    }

    @Provides
    fun providesGetPagerUseCaseImpl(getPagerUseCaseImpl: GetPagerUseCaseImpl): GetPagerUseCase {
        return getPagerUseCaseImpl
    }
}