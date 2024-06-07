package com.example.quickresponsecode.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.RemoteException
import com.example.quickresponsecode.QuickResponseCodeApplication
import com.example.quickresponsecode.data.enums.NetworkType
import com.example.quickresponsecode.data.model.NetworkStatusState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionManager
@Inject
constructor(
    private val context: QuickResponseCodeApplication,
)
{
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    private val appScope: CoroutineScope = CoroutineScope(mainDispatcher)

    private val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var callback: ConnectivityManager.NetworkCallback? = null
    private var receiver: ConnectivityReceiver? = null

    private val _state = MutableStateFlow(getCurrentNetwork())
    val state = _state.asStateFlow()

    init {
        _state
            .subscriptionCount
            .map { count -> count > 0 } // map count into active/inactive flag
            .distinctUntilChanged() // only react to true<->false changes
            .onEach { isActive ->
                /** Only subscribe to network callbacks if we have an active subscriber */
                if (isActive) subscribe()
                else unsubscribe()
            }
            .launchIn(appScope)
    }

    fun hasNetworkConnection() = getCurrentNetwork() is NetworkStatusState.NetworkStatusConnected

    private fun getCurrentNetwork(): NetworkStatusState {
        return try {
            cm.getNetworkCapabilities(cm.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .let { connected ->
                    if (connected == true) NetworkStatusState.NetworkStatusConnected(networkType(context))
                    else NetworkStatusState.NetworkStatusDisconnected
                }
        } catch (e: RemoteException) {
            NetworkStatusState.NetworkStatusDisconnected
        }
    }

    private fun subscribe() {

        // just in case
        if (callback != null || receiver != null) return

        callback = NetworkCallbackImpl().also { cm.registerDefaultNetworkCallback(it) }

        /* emit our initial state */
        emitNetworkState(getCurrentNetwork())
    }

    private fun unsubscribe() {

        if (callback == null && receiver == null) return

        callback?.run { cm.unregisterNetworkCallback(this) }
        callback = null
    }

    private fun emitNetworkState(newState: NetworkStatusState) {
        appScope.launch(mainDispatcher) {
            _state.emit(newState)
        }
    }

    private inner class ConnectivityReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            emitNetworkState(getCurrentNetwork())
        }
    }

    private inner class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            emitNetworkState(NetworkStatusState.NetworkStatusConnected(networkType(context)))
        }

        override fun onLost(network: Network) = emitNetworkState(NetworkStatusState.NetworkStatusDisconnected)
    }

    companion object {
        const val TAG = "NetworkConnectionManager"
        fun networkType(context: Context): NetworkType {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nw = connectivityManager.activeNetwork ?: return NetworkType.UNKNOWN
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return NetworkType.UNKNOWN
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
                else -> NetworkType.UNKNOWN
            }
        }
    }
}