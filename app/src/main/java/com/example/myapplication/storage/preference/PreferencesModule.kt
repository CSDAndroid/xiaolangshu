package com.example.myapplication.storage.preference

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    abstract fun bindPreferencesManager(preferencesManager: PreferencesManager): PreferencesManager

    companion object {
        @Provides
        @Singleton
        fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
            return PreferencesManager(context)
        }
    }
}