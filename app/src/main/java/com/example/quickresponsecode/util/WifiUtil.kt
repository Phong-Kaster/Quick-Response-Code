package com.example.quickresponsecode.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.example.quickresponsecode.R

object WifiUtil {

    /**********************************************
     *  Only work on Android 10 */
    fun connectWifiOnAndroid10(
        context: Context,
        password: String,
        ssid: String,
        onFailed: () -> Unit = {}
    ) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) return

        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                super.onAvailable(network)
                connectivityManager.bindProcessToNetwork(network)
            }

            override fun onLost(network: android.net.Network) {
                super.onLost(network)
                connectivityManager.bindProcessToNetwork(null)
                openWifiSetting(context = context)
            }
        }

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }


    /***********************************
     * Connect to a WiFi network using Android 9 & Below.
     */
    fun connectWifiOnAndroid9AndBelow(context: Context, ssid: String, password: String) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) return

        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"" + ssid + "\"" // Enclose SSID in quotes
        wifiConfig.preSharedKey = "\"" + password + "\"" // Enclose password in quotes

        // Optional: Set security type (WPA2 is common)
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        try {
            val networkId = wifiManager.addNetwork(wifiConfig)
            // Connect successfully
            if (networkId != -1) {
                wifiManager.disconnect()
                wifiManager.enableNetwork(networkId, true)
            } else {
                // Connect failed
                openWifiSetting(context = context)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(context, "Failed to connect to WiFi", Toast.LENGTH_SHORT).show()
        }
    }

    /***********************************
     * Use a full page activity - if Wifi is not available, user will be redirected to settings
     */
    private fun openWifiSetting(context: Context){
        Toast.makeText(context, context.getString(R.string.please_connect_wifi_manually), Toast.LENGTH_SHORT).show()

        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        context.startActivity(intent)
    }
}