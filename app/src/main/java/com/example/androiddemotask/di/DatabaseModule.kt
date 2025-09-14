package com.example.androiddemotask.di

import android.content.Context
import androidx.room.Room
import com.example.androiddemotask.data.local.AppDatabase
import com.example.androiddemotask.data.local.MessageDao
import com.example.androiddemotask.data.local.NewsDao
import com.example.androiddemotask.data.local.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideNewsDao(database: AppDatabase): NewsDao = database.newsDao()

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao = database.messageDao()

    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao = database.userProfileDao()
}
