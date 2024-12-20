package com.example.turaalkalmazas.service.module

import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import com.example.turaalkalmazas.service.GlobalRouteService
import com.example.turaalkalmazas.service.RouteService
import com.example.turaalkalmazas.service.impl.AccountServiceImpl
import com.example.turaalkalmazas.service.impl.FriendsServiceImpl
import com.example.turaalkalmazas.service.impl.GlobalRouteServiceImpl
import com.example.turaalkalmazas.service.impl.RouteServiceImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService
    @Binds abstract fun provideFriendsService(impl: FriendsServiceImpl): FriendsService
    @Binds abstract fun provideRouteService(impl: RouteServiceImpl): RouteService
    @Binds abstract fun provideGlobalRouteService(impl: GlobalRouteServiceImpl): GlobalRouteService

    companion object {
        @Provides
        fun provideFirebaseFirestore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
    }
}