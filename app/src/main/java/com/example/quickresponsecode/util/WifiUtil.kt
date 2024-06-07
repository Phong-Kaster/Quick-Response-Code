package com.example.quickresponsecode.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.PasspointConfiguration
import android.net.wifi.hotspot2.pps.Credential
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.quickresponsecode.R
import com.example.quickresponsecode.data.enums.SecurityLevel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.util.Hashtable


object WifiUtil {

    /*************************************************
     *  Only work on Android 10
     */
    fun connectWifiOnAndroid10(
        context: Context,
        password: String,
        ssid: String,
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


    /*************************************************
     * Connect to a WiFi network using Android 9 & Below.
     */
    fun connectWifiOnAndroid9AndBelow(context: Context, ssid: String?, password: String?) {
        if(ssid.isNullOrEmpty() || password.isNullOrEmpty()) return

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

    /*************************************************
     * Use a full page activity - if Wifi is not available, user will be redirected to settings
     */
    private fun openWifiSetting(context: Context){
        Toast.makeText(context, context.getString(R.string.please_connect_wifi_manually), Toast.LENGTH_SHORT).show()

        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        context.startActivity(intent)
    }


    /*************************************************
     * this raw value is used to generate a QR code
     */
    fun generateWifiRawValue(
        ssid: String,
        encryption: SecurityLevel,
        password: String,
        hidden: Boolean,
    ): String {
        return "WIFI:S:" + ssid.replace("\n+", " ") +
                ";T:" + encryption.value +
                ";P:" + password +
                ";H:" + hidden + ";;"
    }


    /*************************************************
     * encode As Bitmap
     */
    const val WIDTH: Int = 1080
    fun encodeAsBitmap(rawValue: String): Bitmap? {
        try {
            val hints: Hashtable<EncodeHintType, Any> = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.MARGIN] = 2
            val result = MultiFormatWriter().encode(rawValue, BarcodeFormat.QR_CODE, WIDTH, WIDTH, hints)

            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] = if (result[x, y]) Color.BLACK else Color.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h)

            Log.d("PHONG", "QrResultLayout - encodeAsBitmap - bitmap not null")

            return bitmap
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            Log.d("PHONG", "QrResultLayout - encodeAsBitmap - bitmap null")
            return null
        }
    }

    /*************************************************
     * get wifi ssid that device is connecting
     */
    fun getSSID(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid
    }

    /*************************************************
     * create network suggestions
     */
    fun createNetworkSuggestions(password: String, ssid: String): List<WifiNetworkSuggestion>{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return listOf()

        val networkSuggestions = java.util.ArrayList<WifiNetworkSuggestion>()

        val networkSuggestion0 = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
            .build()

        val networkSuggestion1 = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
            .build()

        val networkSuggestion2 = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa3Passphrase(password)
            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
            .build()


        networkSuggestions.add(networkSuggestion0)
        networkSuggestions.add(networkSuggestion1)
        networkSuggestions.add(networkSuggestion2)

        return networkSuggestions
    }


    /*************************************************
     * add Wifi Manually On Android 11 And Higher
     */
    fun addWifiManuallyOnAndroid11AndHigher(
        password: String,
        ssid: String,
        settingLauncher: ActivityResultLauncher<Intent>
    ) {
        /** Below Android 11 is not supported*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        /** There are 3 ways to configure & connect Wifi automatically*/
        val suggestions = ArrayList<WifiNetworkSuggestion>()

        /** 1. WPA2 configuration */
        val wpa2Configuration = WifiNetworkSuggestion.Builder().setSsid(ssid).setWpa2Passphrase(password).build()

        /** 2. Open configuration */
        val openConfiguration = WifiNetworkSuggestion.Builder().setSsid(ssid).build()

        /** 3. Passpoint configuration*/
        val config = PasspointConfiguration()
        config.credential = Credential().apply {
            realm = "realm.example.com"
            simCredential = Credential.SimCredential().apply {
                eapType = 18
                imsi = "123456*"
            }
        }
        config.homeSp = HomeSp().apply {
            fqdn = "test1.example.com"
            friendlyName = "$ssid Host"
        }

        val passpointConfiguration =
            WifiNetworkSuggestion.Builder().setPasspointConfig(config).build()

        /*suggestions.add(passpointConfiguration)*/
        suggestions.add(wpa2Configuration)
        suggestions.add(openConfiguration)

        /** Create intent*/
        val bundle = Bundle()
        bundle.putParcelableArrayList(Settings.EXTRA_WIFI_NETWORK_LIST, suggestions)
        val intent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS)
        intent.putExtra(Settings.EXTRA_WIFI_NETWORK_LIST, suggestions)

        try {
            settingLauncher.launch(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.d("PHONG", "connectWifiOnAndroid11AndAbove - exception: ${ex.message} ")
        }
    }

    /*************************************************
     * open Wifi Panel
     * */
    fun openWifiPanel(context: Context, text: String?,
                              wifiLauncher: ActivityResultLauncher<Intent>){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        AppUtil.copyToClipboard(context = context, text = text ?: "")

        val intent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        wifiLauncher.launch(intent)
    }
}