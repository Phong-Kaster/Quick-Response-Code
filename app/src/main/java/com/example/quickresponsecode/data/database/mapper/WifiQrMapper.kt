package com.example.quickresponsecode.data.database.mapper

import com.example.quickresponsecode.data.database.entity.WifiQrEntity
import com.example.quickresponsecode.data.database.model.WifiQr

object WifiQrMapper {
    fun WifiQrEntity.toModel(): WifiQr {
        return WifiQr(
            id = this.id,
            wifiSSID = this.wifiSSID,
            wifiPassword = this.wifiPassword,
            securityLevel = this.securityLevel,
            hidden = this.hidden,
            method = this.method,
            epochDay = this.epochDay,
            epochMinutes = this.epochMinutes,
        )
    }

    fun WifiQr.toEntity(): WifiQrEntity {
        return WifiQrEntity(
            id = this.id,
            wifiSSID = this.wifiSSID,
            wifiPassword = this.wifiPassword,
            securityLevel = this.securityLevel,
            hidden = this.hidden,
            method = this.method,
            epochDay = this.epochDay,
            epochMinutes = this.epochMinutes,
        )
    }
}