package com.example.quickresponsecode.ui.fragment.qrscan

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickresponsecode.configuration.WifiPassword
import com.example.quickresponsecode.configuration.WifiSSID
import com.example.quickresponsecode.configuration.WifiType
import com.example.quickresponsecode.data.database.model.WifiQr
import com.example.quickresponsecode.data.enums.Method
import com.example.quickresponsecode.data.enums.NetworkType
import com.example.quickresponsecode.data.enums.SecurityLevel
import com.example.quickresponsecode.data.model.NetworkStatusState
import com.example.quickresponsecode.data.repository.NetworkConnectionManager
import com.example.quickresponsecode.data.repository.SettingRepository
import com.example.quickresponsecode.data.repository.WifiQrRepository
import com.example.quickresponsecode.util.ScannerUtil.toInputImage
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QrScanViewModel
@Inject
constructor(
    private val wifiQrRepository: WifiQrRepository,
    private val networkConnectionManager: NetworkConnectionManager,
    private val settingRepository: SettingRepository,
) : ViewModel() {

    /*************************************************
     * show Toast
     */
    private var _showToast = MutableStateFlow<Boolean>(false)
    val showToast = _showToast.asStateFlow()


    /*************************************************
     * network Status
     */
    private var _networkStatus = MutableStateFlow<NetworkType>(NetworkType.UNKNOWN)
    val networkStatus = _networkStatus.asStateFlow()

    /*************************************************
     * enable Rationale Dialog
     */
    private var _enableAnimation = MutableStateFlow<Boolean>(false)
    val enableAnimation = _enableAnimation.asStateFlow()

    /*************************************************
     * enable Rationale Dialog
     */
    private var _enableRationaleDialog = MutableStateFlow<Boolean>(false)
    val enableRationaleDialog = _enableRationaleDialog.asStateFlow()


    /*************************************************
     * previous Network Status represents the status before users click Connect
     * latest Network Status represents the status after users click Connect
     */
    private var _previousNetworkState = MutableStateFlow<NetworkStatusState>(NetworkStatusState.NetworkStatusDisconnected)
    val previousNetworkState = _previousNetworkState.asStateFlow()

    private var _latestNetworkState = MutableStateFlow<NetworkStatusState>(NetworkStatusState.NetworkStatusDisconnected)
    val latestNetworkState = _latestNetworkState.asStateFlow()

    /*************************************************
     * Configure scanner
     */
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_ALL_FORMATS)
        .enableAllPotentialBarcodes() // Optional
        .build()

    val scanner: BarcodeScanner by lazy { BarcodeScanning.getClient(options) }

    init {
        collectNetworkStatus()
        enableRationaleDialog()

        collectPreviousNetworkState()
        collectLatestNetworkState()
    }



    private fun collectNetworkStatus() {
        viewModelScope.launch {
            networkConnectionManager.state
                .collectLatest {
                    if (it is NetworkStatusState.NetworkStatusConnected) {
                        //Log.d("PHONG", "NetworkStatusState: ${it.type}")
                        _networkStatus.value = it.type
                        _latestNetworkState.value = it
                        _enableAnimation.value = false
                    } else {
                        //Log.d("PHONG", "NetworkStatusState: $it")
                        _networkStatus.value = NetworkType.UNKNOWN
                        _latestNetworkState.value = NetworkStatusState.NetworkStatusDisconnected
                        _enableAnimation.value = false
                    }
                }
        }
    }

    /*************************************************
     * this function get network state before users click Connect
     */
    private fun collectPreviousNetworkState() {
        viewModelScope.launch {
            _previousNetworkState.value =  networkConnectionManager.state.value
        }
    }

    /*************************************************
     * this function get network state after users click Connect
     */
    private fun collectLatestNetworkState() {
        viewModelScope.launch {
            networkConnectionManager.state
                .collectLatest {
                    if (it is NetworkStatusState.NetworkStatusConnected) {
                        _latestNetworkState.value = it
                    } else {
                        _latestNetworkState.value = NetworkStatusState.NetworkStatusDisconnected
                    }
                }
        }
    }

    /*************************************************
     * processPhoto - this function process photo when app scans any Wifi QR
     */
    fun processPhoto(
        imageProxy: ImageProxy?,
        gotoNextScreen: (WifiSSID, WifiPassword, WifiType) -> Unit = { _, _, _ -> },
    ) {
        //Log.d("SCANNER", "----------------------")
        if (imageProxy == null) {
            _showToast.value = true
            return
        }


        val inputImage = imageProxy.toInputImage()
        if (inputImage == null) {
            _showToast.value = true
            imageProxy.close()
            return
        }


        scanner
            .process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    imageProxy.close()
                    return@addOnSuccessListener
                }

                for (barcode in barcodes) {
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue
                    val valueType: Int = barcode.valueType


                    if (valueType == Barcode.TYPE_WIFI) {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        val type = barcode.wifi!!.encryptionType


                        Log.d("SCANNER", "Barcode.TYPE_WIFI")
                        Log.d("SCANNER", "ssid: $ssid")
                        Log.d("SCANNER", "password: $password")

                        _showToast.value = false
                        insertWifiQR(wifiSSID = ssid ?: "", wifiPassword = password ?: "")
                        gotoNextScreen(ssid ?: "", password ?: "", type)
                        break
                    } else if (
                        valueType == Barcode.TYPE_EMAIL ||
                        valueType == Barcode.TYPE_PHONE ||
                        valueType == Barcode.TYPE_ISBN ||
                        valueType == Barcode.TYPE_PRODUCT ||
                        valueType == Barcode.TYPE_CALENDAR_EVENT ||
                        valueType == Barcode.TYPE_DRIVER_LICENSE ||
                        valueType == Barcode.TYPE_GEO ||
                        valueType == Barcode.TYPE_URL ||
                        valueType == Barcode.TYPE_SMS ||
                        valueType == Barcode.TYPE_CONTACT_INFO
                    ) {
                        Log.d("SCANNER", "Barcode.TYPE_NOT_VALID with value type $valueType")
                        _showToast.value = true
                        imageProxy.close()
                    } else {
                        imageProxy.close()
                    }
                }
            }
            .addOnFailureListener { exception: Exception ->
                Log.d("SCANNER", "Home View Model - addOnFailureListener")
                imageProxy.close()
                exception.printStackTrace()
            }
    }


    /*************************************************
     * processPhotoFromGallery - this function process photo when app receive a photo from gallery
     */
    fun processPhotoFromGallery(
        inputImage: InputImage,
        gotoNextScreen: (WifiSSID, WifiPassword) -> Unit = { _, _ -> }
    ) {
        Log.d("PHONG", "processPhotoFromGallery ------------------- ")
        scanner
            .process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    _showToast.value = true
                    return@addOnSuccessListener
                }

                for (barcode in barcodes) {
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue

                    val valueType: Int = barcode.valueType
                    Log.d("PHONG", "processPhoto - rawValue: $rawValue")


                    if (valueType == Barcode.TYPE_WIFI) {
                        val ssid = barcode.wifi?.ssid
                        val password = barcode.wifi?.password
                        val type = barcode.wifi?.encryptionType


                        Log.d("PHONG", "---------------------")
                        Log.d("PHONG", "Barcode.TYPE_WIFI")
                        Log.d("PHONG", "ssid: $ssid")
                        Log.d("PHONG", "password: $password")

                        _showToast.value = false
                        gotoNextScreen(ssid ?: "", password ?: "")
                        insertWifiQR(wifiSSID = ssid ?: "", wifiPassword = password ?: "")
                    } else {
                        Log.d("PHONG", "Barcode.TYPE_NOT_VALID with value type $valueType")
                        _showToast.value = true
                    }
                }
            }
            .addOnFailureListener { exception: Exception ->
                Log.d("PHONG", "Home View Model - addOnFailureListener")
                exception.printStackTrace()
            }
    }

    fun closeToast() { _showToast.value = false }
    fun disableAnimation() { _enableAnimation.value = false }
    fun enableAnimation() { _enableAnimation.value = true }

    fun resetConnectMethod() { _networkStatus.value = NetworkType.UNKNOWN }

    /*************************************************
     * insert a Wifi QR into database
     */
    private fun insertWifiQR(wifiSSID: WifiSSID, wifiPassword: WifiPassword) {
        viewModelScope.launch(Dispatchers.IO) {

            val wifiQr =  WifiQr(
                id = 0,
                wifiSSID = wifiSSID,
                wifiPassword = wifiPassword,
                securityLevel = SecurityLevel.WPAWPA2,
                hidden = false,
                method = Method.Scan
            )

            wifiQrRepository.insertOrUpdate(wifiQr)
        }
    }

    /*************************************************
     * disable rationale dialog
     */
    fun disableRationaleDialog(){
        viewModelScope.launch(Dispatchers.IO){
            _enableRationaleDialog.value = false
            settingRepository.disableRationaleDialog()
        }
    }

    /*************************************************
     * this rationale dialog in this screen shows only one time
     */
    private fun enableRationaleDialog(){
        viewModelScope.launch(Dispatchers.IO){
            _enableRationaleDialog.value = settingRepository.enableRationaleDialog()
        }
    }
}