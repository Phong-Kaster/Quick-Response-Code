package com.example.quickresponsecode.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quickresponsecode.data.database.converter.WifiQrConverter
import com.example.quickresponsecode.data.database.dao.WifiQrDao
import com.example.quickresponsecode.data.database.entity.WifiQrEntity

@Database(
    entities = [WifiQrEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(WifiQrConverter::class)
abstract class WifiQrDatabase : RoomDatabase() {
    abstract fun wifiQRDao(): WifiQrDao
}