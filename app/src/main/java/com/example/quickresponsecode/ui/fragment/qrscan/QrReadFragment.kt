package com.example.quickresponsecode.ui.fragment.qrscan

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.data.enums.NetworkType
import com.example.quickresponsecode.data.model.NetworkStatusState
import com.example.quickresponsecode.ui.component.CoreTopBar2
import com.example.quickresponsecode.ui.component.SolidButton
import com.example.quickresponsecode.util.AppUtil
import com.example.quickresponsecode.util.NavigationUtil.safeNavigate
import com.example.quickresponsecode.util.NavigationUtil.safeNavigateUp
import com.example.quickresponsecode.util.WifiUtil
import com.panda.wifipassword.ui.screen.qr.qrscan.component.QrLoadingDialog
import com.panda.wifipassword.ui.screen.qr.qrscan.component.QrRationaleDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Save networks and Passpoint configurations - https://developer.android.com/develop/connectivity/wifi/wifi-save-network-passpoint-config
 *
 * Issue: Crash happens on Realme 9i (Android 13) that have no solution
 */
@AndroidEntryPoint
class QrReadFragment : CoreFragment() {

    private val viewModel: QrScanViewModel by viewModels()
    private var ssid: String? by mutableStateOf(null)
    private var password: String? by mutableStateOf(null)


    private var ssidCurrent: String? by mutableStateOf(null)
    private lateinit var wifiManager: WifiManager
    private var showRationaleDialog by mutableStateOf(false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        getWifiInfo()
    }

    private fun getWifiInfo() {
        ssid = arguments?.getString("wifiSSID")
        password = arguments?.getString("wifiPassword")
        ssidCurrent = WifiUtil.getSSID(context = requireActivity())
    }


    /*************************************************
     * Save networks and Passpoint configurations - https://developer.android.com/develop/connectivity/wifi/wifi-save-network-passpoint-config
     */
    @RequiresApi(Build.VERSION_CODES.R)
    private val settingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                // user agreed to save configurations: still need to check individual results
                if (result.data != null && result.data!!.hasExtra(Settings.EXTRA_WIFI_NETWORK_RESULT_LIST)) {
                    for (code in result.data!!.getIntegerArrayListExtra(
                        Settings.EXTRA_WIFI_NETWORK_RESULT_LIST)!!) {
                        when (code) {
                            Settings.ADD_WIFI_RESULT_SUCCESS -> Log.d("PHONG", "ADD_WIFI_RESULT_SUCCESS - Configuration saved or modified")
                            Settings.ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED -> Log.d("PHONG", "ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED - Something went wrong - invalid configuration ")
                            Settings.ADD_WIFI_RESULT_ALREADY_EXISTS -> {
                                Log.d("PHONG", "ADD_WIFI_RESULT_ALREADY_EXISTS - Configuration existed (as-is) on device, nothing changed")
                                gotoNextScreen()
                            }
                            else -> Log.d("PHONG", "Fail")
                        }
                    }
                }
            } else {
                Log.d("PHONG", "User refused to save configurations")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /*************************************************
     * wifiLauncher is used in Wifi.openWifiPanel()
     */
    private val wifiLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        ssidCurrent = WifiUtil.getSSID(context = requireActivity())

        /*if(ssidCurrent?.contains(ssid!!) == false) {
            showToast(getString(R.string.connect_wifi_failed_please_try_again))
            return@registerForActivityResult
        }*/

        val latestNetworkState = viewModel.latestNetworkState.value
        if( latestNetworkState is NetworkStatusState.NetworkStatusDisconnected ) return@registerForActivityResult

        gotoNextScreen()
    }


    /*************************************************
     * add Wifi Automatically On Android 10 And Above
     */
    private fun addWifiAutomaticallyOnAndroid10AndAbove(password: String?, ssid: String?) {
        if (password.isNullOrEmpty() || ssid.isNullOrEmpty()) {
            showToast(getString(R.string.password_or_ssid_is_empty))
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        val currentWifiInfo = wifiManager.connectionInfo
        Log.d("PHONG", "addWifiAutomaticallyOnAndroid10AndAbove - currentWifiInfo.ssid ${currentWifiInfo.ssid} ")
        /* Case 1: scanned wifi is same with current wifi connection then go to next screen */
        if (currentWifiInfo.ssid.contains(ssid)) {
            Log.d("PHONG", "Case 1: scanned wifi is same with current wifi connection then go to next screen ")
            gotoNextScreen()
            return
        }


        /* Case 2: scan wifi is different with current wifi connection then open Setting Panel to users change manually */
        if (currentWifiInfo != null && currentWifiInfo.networkId != -1) {
            Log.d("PHONG", "Case 2: scan wifi is different with current wifi connection then open Setting Panel to users change manually")
            WifiUtil.openWifiPanel(context = requireContext(), text = password, wifiLauncher = wifiLauncher)
            return
        }


        /* Case 3: there is no any Wifi connection then automatically connect to new wifi */
        Log.d("PHONG", "Case 3: there is no any Wifi connection")
        val networkSuggestions = WifiUtil.createNetworkSuggestions(ssid = ssid, password = password)
        val status = wifiManager.addNetworkSuggestions(networkSuggestions)
        when (status) {
            WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS -> {
                Log.d("PHONG", "status == STATUS_NETWORK_SUGGESTIONS_SUCCESS")
                setupAnimation()
            }
            else -> {
                Log.d("PHONG", "status == STATUS_NETWORK_SUGGESTIONS_NOT_SUCCESS ")
                showToast(getString(R.string.connect_wifi_failed_please_try_again))

                when {
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                        WifiUtil.openWifiPanel(context = requireContext(), text = password, wifiLauncher = wifiLauncher)
                    } // Android 10
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> WifiUtil.addWifiManuallyOnAndroid11AndHigher(ssid = ssid, password = password, settingLauncher = settingLauncher) // Android 11 and higher
                    else -> {  } // Other Android
                }
            }
        }
    }


    /*************************************************
     * enable loading after 5 seconds
     * */
    private val animationHandler = Handler(Looper.getMainLooper())
    private var hasAnimationEnabled = AtomicBoolean(false)
    private val enableAnimationAfter5s = Runnable {
        Log.d("PHONG", "enable animation after 5s")
        viewModel.enableAnimation()
    }

    private val disableAnimationAfter30s = Runnable {
        Log.d("PHONG", "disable animation after 30s")
        viewModel.disableAnimation()

        hasAnimationEnabled.set(false)
        showToast(getString(R.string.connect_wifi_failed_please_enter_password_manually))
        WifiUtil.openWifiPanel(context = requireContext(), wifiLauncher = wifiLauncher, text = password)
    }

    private fun setupAnimation(){
        if (hasAnimationEnabled.getAndSet(true)) return
        // enable animation in 30s. When time out, disable animation & open Setting Panel if no wifi connected
        animationHandler.postDelayed(enableAnimationAfter5s, 5000)
        animationHandler.postDelayed(disableAnimationAfter30s, 30000)
    }


    /*************************************************
     * goto Next Screen
     * */
    private fun gotoNextScreen(){
        safeNavigate(
            destination = R.id.toQrSuccess,
            bundle = bundleOf("wifiSSID" to ssid, "wifiPassword" to password),
            navOptions = NavOptions.Builder().setPopUpTo(R.id.qrReadFragment, true).build()
        )
    }

    @Composable
    override fun ComposeView() {
        val networkStatus = viewModel.networkStatus.collectAsStateWithLifecycle().value
        val enableRationaleDialog = viewModel.enableRationaleDialog.collectAsStateWithLifecycle().value

        val previousNetworkState = viewModel.previousNetworkState.collectAsStateWithLifecycle().value
        val latestNetworkState = viewModel.latestNetworkState.collectAsStateWithLifecycle().value


        QrReadLayout(
            wifiSSID = ssid ?: "",
            wifiPassword = password ?: "",
            onBack = { safeNavigateUp() },
            onConnect = {
                /** From Android 10 and higher */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (enableRationaleDialog) {
                        showRationaleDialog = true
                        viewModel.disableRationaleDialog()
                    } else {
                        addWifiAutomaticallyOnAndroid10AndAbove(ssid = ssid, password = password)
                    }
                }
                else {
                    /** From Android 9 and below */
                    WifiUtil.connectWifiOnAndroid9AndBelow(context = requireContext(), ssid = ssid, password = password)
                }
            },
            onCopyToClipboard = {
                AppUtil.copyToClipboard(
                    context = requireContext(),
                    text = password ?: ""
                )
            }
        )

        /* Automatically go to next screen after wifi is connected
        * Case 1: device has no internet connection, latest connection is Wifi then go to next screen
        * Case 2: device has internet connection, latest connection is the same with current wifi connection then go to next screen */
        LaunchedEffect(
            key1 = networkStatus,
            block = {
                if (networkStatus != NetworkType.WIFI) return@LaunchedEffect
                animationHandler.removeCallbacks(disableAnimationAfter30s)

                if (previousNetworkState is NetworkStatusState.NetworkStatusDisconnected &&
                    latestNetworkState is NetworkStatusState.NetworkStatusConnected
                ) {
                    Log.d("PHONG", "go to next screen with Case 1")
                    viewModel.disableAnimation()
                    gotoNextScreen()
                    viewModel.resetConnectMethod()
                    return@LaunchedEffect
                }


                if (previousNetworkState !is NetworkStatusState.NetworkStatusConnected &&
                    latestNetworkState !is NetworkStatusState.NetworkStatusConnected
                ) {
                    return@LaunchedEffect
                }

                Log.d("PHONG", "go to next screen with Case 2 - ssidCurrent $ssidCurrent & ssid $ssid")
                if (ssidCurrent == ssid || ssidCurrent?.isEmpty() == true) {
                    viewModel.disableAnimation()
                    gotoNextScreen()
                    viewModel.resetConnectMethod()
                }
            }
        )

        QrLoadingDialog(
            enable = viewModel.enableAnimation.collectAsStateWithLifecycle().value,
            onDismissRequest = {
                viewModel.disableAnimation()
                hasAnimationEnabled.set(false)
                animationHandler.removeCallbacks(disableAnimationAfter30s)
            }
        )

        QrRationaleDialog(
            enable = showRationaleDialog,
            onDismissRequest = { showRationaleDialog = false },
            onConfirm = { addWifiAutomaticallyOnAndroid10AndAbove(password = password, ssid = ssid) }
        )
    }
}

@Composable
fun QrReadLayout(
    wifiSSID: String,
    wifiPassword: String,

    onBack: () -> Unit = {},
    onConnect: () -> Unit = {},
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