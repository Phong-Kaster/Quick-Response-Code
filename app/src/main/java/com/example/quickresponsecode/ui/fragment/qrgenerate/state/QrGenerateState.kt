package com.example.quickresponsecode.ui.fragment.qrgenerate.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quickresponsecode.data.enums.SecurityLevel

@Stable
class QrGenerateState {
    var id by mutableLongStateOf(0L)
    var name: String by mutableStateOf("")
    var password: String by mutableStateOf("")
    var securityLevel: SecurityLevel by mutableStateOf(SecurityLevel.WPAWPA2)
    var isHidden: Boolean by mutableStateOf(false)
}