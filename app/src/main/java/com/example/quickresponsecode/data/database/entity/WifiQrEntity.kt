package com.example.quickresponsecode.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.quickresponsecode.data.enums.Method
import com.example.quickresponsecode.data.enums.SecurityLevel
import com.example.quickresponsecode.util.LocalDateUtil.elapsedMinutes
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "table_wifi_qr_code")
data class WifiQrEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "wifiName") val wifiName: String = "",
    @ColumnInfo(name = "wifiPassword") val wifiPassword: String = "",
    @ColumnInfo(name = "securityLevel") val securityLevel: SecurityLevel = SecurityLevel.WPAWPA2,
    @ColumnInfo(name = "hidden") val hidden: Boolean = true,
    @ColumnInfo(name = "method") val method: Method = Method.Generate,
    @ColumnInfo(name = "epochDay") val epochDay: Long = LocalDate.now().toEpochDay(),
    @ColumnInfo(name = "epochMinutes") val epochMinutes: Int = LocalDateTime.now().elapsedMinutes()
)