package com.example.quickresponsecode.injection

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quickresponsecode.data.database.WifiQrDatabase
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
    fun provideWifiQRDatabase(@ApplicationContext context: Context): WifiQrDatabase {
        return Room.databaseBuilder(context, WifiQrDatabase::class.java, "WifiQRDatabase")
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()
    }
}