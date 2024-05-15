package com.example.quickresponsecode.ui.fragment.qrscan

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.PasspointConfiguration
import android.net.wifi.hotspot2.pps.Credential
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_WIFI_ADD_NETWORKS
import android.provider.Settings.ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED
import android.provider.Settings.ADD_WIFI_RESULT_ALREADY_EXISTS
import android.provider.Settings.ADD_WIFI_RESULT_SUCCESS
import android.provider.Settings.EXTRA_WIFI_NETWORK_LIST
import android.provider.Settings.EXTRA_WIFI_NETWORK_RESULT_LIST
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.component.CoreTopBar2
import com.example.quickresponsecode.ui.component.OutlineButton
import com.example.quickresponsecode.ui.component.SolidButton
import com.example.quickresponsecode.util.AppUtil
import com.example.quickresponsecode.util.NavigationUtil.safeNavigateUp
import com.example.quickresponsecode.util.WifiUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * Save networks and Passpoint configurations - https://developer.android.com/develop/connectivity/wifi/wifi-save-network-passpoint-config
 *
 * Issue: Crash happens on Realme 9i (Android 13) that have no solution
 */
@AndroidEntryPoint
class QrReadFragment : CoreFragment() {

    private var ssid: String? by mutableStateOf(null)
    private var password: String? by mutableStateOf(null)
    private var type: Int? by mutableStateOf(null)

    private lateinit var wifiManager: WifiManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        getWifiInfo()
    }

    private fun getWifiInfo() {
        ssid = arguments?.getString("wifiSSID")
        password = arguments?.getString("wifiPassword")
        type = arguments?.getInt("wifiType")
    }


    private fun connectWifi(password: String?, ssid: String?) {
        if (password.isNullOrEmpty() || ssid.isNullOrEmpty()) {
            showToast("Password or SSID is empty !")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            /** From Android 11 and higher */
            connectWifiOnAndroid11AndAbove(
                ssid = ssid,
                password = password
            )
            /*connectToWifi(ssid, password)*/
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            /** For Android 10 only*/
            WifiUtil.connectWifiOnAndroid10(
                context = requireContext(),
                ssid = ssid,
                password = password
            )
        } else {
            /** From Android 9 and below */
            WifiUtil.connectWifiOnAndroid9AndBelow(
                context = requireContext(),
                ssid = ssid,
                password = password
            )
        }
    }

    /**
     * Save networks and Passpoint configurations - https://developer.android.com/develop/connectivity/wifi/wifi-save-network-passpoint-config
     */
    @RequiresApi(Build.VERSION_CODES.R)
    private val settingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val resultCode = result.resultCode
                if (resultCode == RESULT_OK) {
                    // user agreed to save configurations: still need to check individual results
                    if (result.data != null && result.data!!.hasExtra(EXTRA_WIFI_NETWORK_RESULT_LIST)) {
                        for (code in result.data!!.getIntegerArrayListExtra(
                            EXTRA_WIFI_NETWORK_RESULT_LIST
                        )!!) {
                            when (code) {
                                ADD_WIFI_RESULT_SUCCESS -> {
                                    // Configuration saved or modified
                                    showToast("ADD_WIFI_RESULT_SUCCESS")
                                }

                                ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED -> {
                                    // Something went wrong - invalid configuration
                                    showToast("ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED")
                                }

                                ADD_WIFI_RESULT_ALREADY_EXISTS -> {
                                    // Configuration existed (as-is) on device, nothing changed
                                    showToast("ADD_WIFI_RESULT_ALREADY_EXISTS")
                                }

                                else -> showToast("Failed")
                            }
                        }
                    }
                } else {
                    // User refused to save configurations
                    showToast("User refused to save configurations")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    private fun connectWifiOnAndroid11AndAbove(password: String, ssid: String) {
        /** Below Android 11 is not supported*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        /** There are 3 ways to configure & connect Wifi automatically*/
        val suggestions = ArrayList<WifiNetworkSuggestion>()

        /** 1. WPA2 configuration */
        val wpa2Configuration =
            WifiNetworkSuggestion.Builder().setSsid(ssid).setWpa2Passphrase(password).build()

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
            friendlyName = "Some Friendly Name"
        }

        val passpointConfiguration =
            WifiNetworkSuggestion.Builder().setPasspointConfig(config).build()

        suggestions.add(passpointConfiguration)
        suggestions.add(wpa2Configuration)
        suggestions.add(openConfiguration)

        /** Create intent*/
        val bundle = Bundle()
        bundle.putParcelableArrayList(EXTRA_WIFI_NETWORK_LIST, suggestions)
        val intent = Intent(ACTION_WIFI_ADD_NETWORKS)
        intent.putExtra(EXTRA_WIFI_NETWORK_LIST, suggestions)

        try {
            settingLauncher.launch(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.d("PHONG", "connectWifiOnAndroid11AndAbove - exception: ${ex.message} ")
        }
    }

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        QrReadLayout(
            wifiSSID = ssid ?: "",
            wifiPassword = password ?: "",
            onBack = { safeNavigateUp() },
            onConnect = { connectWifi(ssid = ssid, password = password) },
            onShare = { showToast(requireContext().getString(R.string.share_with_my_community)) },
            onCopyToClipboard = { AppUtil.copyToClipboard(context = requireContext(), text = password ?: "") }
        )
    }
}

@Composable
fun QrReadLayout(
    wifiSSID: String,
    wifiPassword: String,

    onBack: () -> Unit = {},
    onConnect: () -> Unit = {},

    onShare: () -> Unit = {},
    onCopyToClipboard: () -> Unit = {}
) {
    CoreLayout(
        topBar = {
            CoreTopBar2(
                text = stringResource(R.string.result),
                textColor = Color.White,
                textArrangement = Arrangement.Center,
                iconLeft = R.drawable.ic_back,
                iconLeftColor = Color.White,
                iconLeftColorBackground = Color.Transparent,
                onLeftClick = onBack,
                onRightClick = {},
                iconRight = R.drawable.ic_back,
                iconRightColor = Color.Transparent,
                iconRightColorBackground = Color.Transparent,
            )
        },
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF004BDC),
                Color(0xFF8DB4FF),
            ),
        ),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(color = Color.White)
                    .padding(24.dp)
            ) {
                // QR EXAMPLE PHOTO
                Image(
                    painter = painterResource(id = R.drawable.img_qr_example),
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .aspectRatio(1F)
                )


                // WIFI NAME & PASSWORD
                Column(
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xFFF4F4F4))
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.name),
                            style = TextStyle(
                                color = Color(0xFF8A8A8A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400)
                            )
                        )

                        Text(
                            text = wifiSSID,
                            style = TextStyle(
                                color = Color(0xFF333333),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500)
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.password),
                            style = TextStyle(
                                color = Color(0xFF8A8A8A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400)
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                        ) {
                            Text(
                                text = wifiPassword,
                                style = TextStyle(
                                    color = Color(0xFF333333),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(500)
                                )
                            )


                            Icon(
                                painter = painterResource(id = R.drawable.ic_copy_to_clipboard),
                                contentDescription = null,
                                tint = Color(0xFF6B6B6B),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(15.dp))
                                    .clickable { onCopyToClipboard() }
                            )
                        }
                    }
                }


                SolidButton(
                    modifier = Modifier.fillMaxWidth(),
                    marginHorizontal = 0.dp,
                    onClick = onConnect,
                    backgroundColor = Color(0xFF1C68FB),
                    textColor = Color.White,
                    text = stringResource(id = R.string.connect),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600)
                    ),
                    shape = RoundedCornerShape(25.dp)
                )

                OutlineButton(
                    text = stringResource(R.string.share_with_my_community),
                    modifier = Modifier.fillMaxWidth(),
                    marginHorizontal = 0.dp,
                    marginVertical = 0.dp,
                    borderStroke = BorderStroke(width = 1.dp, color = Color(0xFF1C68FB)),
                    onClick = onShare,
                    backgroundColor = Color.White,
                    textColor = Color(0xFF1C68FB),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600)
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
            }
        })
}

@Preview
@Composable
private fun PreviewQrRead() {
    QrReadLayout(
        wifiSSID = "Wifi Name",
        wifiPassword = "Wifi Password",
    )
}