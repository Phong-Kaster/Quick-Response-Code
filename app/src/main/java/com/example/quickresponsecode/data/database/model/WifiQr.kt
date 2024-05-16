package com.example.quickresponsecode.data.database.model

import androidx.compose.runtime.Immutable
import com.example.quickresponsecode.data.enums.Method
import com.example.quickresponsecode.data.enums.SecurityLevel
import com.example.quickresponsecode.util.LocalDateUtil.elapsedMinutes
import java.time.LocalDate
import java.time.LocalDateTime

@Immutable
class WifiQr
constructor(
    val id: Long,
    val wifiName: String = "",
    val wifiPassword: String = "",
    val securityLevel: SecurityLevel = SecurityLevel.WPAWPA2,
    val hidden: Boolean = true,
    val method: Method = Method.Generate,
    val epochDay: Long = LocalDate.now().toEpochDay(),
    val epochMinutes: Int = LocalDateTime.now().elapsedMinutes()
) {

}