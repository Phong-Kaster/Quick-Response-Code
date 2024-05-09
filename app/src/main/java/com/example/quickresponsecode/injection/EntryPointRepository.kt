package com.example.quickresponsecode.injection

import com.example.quickresponsecode.data.repository.SettingRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface EntryPointRepository {
    fun settingRepository(): SettingRepository
}