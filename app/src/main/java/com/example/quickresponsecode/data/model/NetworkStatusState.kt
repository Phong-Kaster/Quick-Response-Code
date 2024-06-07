package com.example.quickresponsecode.data.model

import com.example.quickresponsecode.data.enums.NetworkType

sealed class NetworkStatusState {

    /* Device has a valid internet connection */
    class NetworkStatusConnected(val type: NetworkType) : NetworkStatusState()

    /* Device has no internet connection */
    object NetworkStatusDisconnected : NetworkStatusState()
}