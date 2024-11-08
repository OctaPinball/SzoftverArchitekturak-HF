package com.example.turaalkalmazas.service.module

import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.impl.AccountServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService
}