package com.example.quickresponsecode.data.enums

import androidx.annotation.StringRes
import com.example.quickresponsecode.R

enum class SecurityLevel(
    @StringRes val text: Int,
    val value: String,
){
    WPAWPA2(text = R.string.wpa_wpa_2, value = "WPA"),
    WEP(text = R.string.wep, value = "WEP"),
    NONE(text = R.string.none, value = "OPEN"),

    ;
    companion object {
        fun valueOfOrDefault(value: String): SecurityLevel {
            return try {
                valueOf(value)
            } catch (_: Exception) {
                WPAWPA2
            }
        }
    }
}