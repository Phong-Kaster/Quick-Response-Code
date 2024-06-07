package com.example.quickresponsecode.data.enums

import androidx.annotation.StringRes
import com.example.quickresponsecode.R

enum class NetworkType(@StringRes val title: Int) {
    WIFI(R.string.wifi),
    CELLULAR(R.string.cellular),
    ETHERNET(R.string.ethernet),
    UNKNOWN(R.string.unknown)
}