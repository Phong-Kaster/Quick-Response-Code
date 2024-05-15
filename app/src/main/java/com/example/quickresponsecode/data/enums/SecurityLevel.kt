package com.example.quickresponsecode.data.enums

import androidx.annotation.StringRes
import com.example.quickresponsecode.R

enum class SecurityLevel(
    @StringRes val text: Int,
){
    WPAWPA2(text = R.string.wpa_wpa_2),
    WEP(text = R.string.wep),
    NONE(text = R.string.none),
}