package com.example.quickresponsecode.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quickresponsecode.data.database.entity.WifiQrEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiQrDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: WifiQrEntity): Long

    @Delete
    fun delete(record: WifiQrEntity)

    @Query("SELECT * FROM table_wifi_qr_code ORDER BY table_wifi_qr_code.epochDay DESC ")
    fun findAll(): List<WifiQrEntity>

    @Query("SELECT * FROM table_wifi_qr_code ORDER BY table_wifi_qr_code.epochDay DESC ")
    fun findAllFlow(): Flow<List<WifiQrEntity>>

    @Query("SELECT * FROM table_wifi_qr_code WHERE :beginDay <= table_wifi_qr_code.epochDay AND table_wifi_qr_code.epochDay <= :finishDay ORDER BY table_wifi_qr_code.epochDay DESC ")
    fun findFromDateToDate(beginDay: Long, finishDay: Long): List<WifiQrEntity>

    @Query("SELECT * FROM table_wifi_qr_code WHERE table_wifi_qr_code.id = :uid")
    fun findWithID(uid: Long): WifiQrEntity
}