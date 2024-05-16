package com.example.quickresponsecode.data.repository

import com.example.quickresponsecode.data.database.WifiQrDatabase
import com.example.quickresponsecode.data.database.dao.WifiQrDao
import com.example.quickresponsecode.data.database.mapper.WifiQrMapper.toEntity
import com.example.quickresponsecode.data.database.mapper.WifiQrMapper.toModel
import com.example.quickresponsecode.data.database.model.WifiQr
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiQrRepository
@Inject
constructor(
    private val wifiQrDatabase: WifiQrDatabase
) {
    private val wifiQrDao = wifiQrDatabase.wifiQRDao()

    fun getAll(): List<WifiQr> {
        return wifiQrDao.findAll().map { entity ->
            entity.toModel()
        }
    }

    fun getAllFlow(): Flow<List<WifiQr>> {
        return wifiQrDao.findAllFlow().map { list ->
            list.map { entity ->
                entity.toModel()
            }
        }
    }

    suspend fun insertOrUpdate(wifiQr: WifiQr): Long {
        return wifiQrDao.insertOrUpdate(wifiQr.toEntity())
    }

    fun delete(wifiQr: WifiQr) {
        wifiQrDao.delete(wifiQr.toEntity())
    }

    fun findWithID(id: Long): WifiQr {
        return wifiQrDao.findWithID(id).toModel()
    }

    fun findFromDateToDate(from: Long, to: Long): List<WifiQr> {
        return wifiQrDao
            .findFromDateToDate(beginDay = from, finishDay = to)
            .map { entity -> entity.toModel() }
    }
}