package com.example.quickresponsecode.data.database.model

import androidx.compose.runtime.Immutable
import com.example.quickresponsecode.data.enums.Method
import com.example.quickresponsecode.data.enums.SecurityLevel
import com.example.quickresponsecode.util.LocalDateUtil.toElapsedMinutes
import java.time.LocalDate
import java.time.LocalDateTime

@Immutable
data class WifiQr(
    val id: Long,
    val wifiSSID: String = "",
    val wifiPassword: String = "",
    val securityLevel: SecurityLevel,
    val hidden: Boolean = true,
    val method: Method,
    val epochDay: Long = LocalDate.now().toEpochDay(),
    val epochMinutes: Int = LocalDateTime.now().toElapsedMinutes()
) {
    companion object {
        fun fakeWifi(): WifiQr{
            return WifiQr(
                id = 0,
                wifiSSID = "example",
                wifiPassword = "password",
                securityLevel = SecurityLevel.WPAWPA2,
                hidden = true,
                method = Method.Generate,
                epochDay = LocalDate.now().toEpochDay(),
                epochMinutes = LocalDateTime.now().toElapsedMinutes()
            )
        }
    }
}