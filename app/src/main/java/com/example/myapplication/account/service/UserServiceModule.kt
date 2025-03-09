package com.example.myapplication.account.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserServiceModule {

    @Binds
    abstract fun bindUserService(userServiceImpl: UserServiceImpl): UserService
}