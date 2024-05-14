package com.example.quickresponsecode.ui.fragment.qr

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickresponsecode.QuickResponseCodeApplication
import com.example.quickresponsecode.configuration.WifiSSID
import com.example.quickresponsecode.configuration.WifiPassword
import com.example.quickresponsecode.configuration.WifiType
import com.example.quickresponsecode.util.AppUtil
import com.example.quickresponsecode.util.ScannerUtil.toInputImage
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QrViewModel
@Inject
constructor(
    private val context: QuickResponseCodeApplication
) : ViewModel() {

    private var _showToast = MutableStateFlow<Boolean>(false)
    val showToast = _showToast.asStateFlow()

    private var _isInternetConnected = MutableStateFlow<Boolean>(false)
    val isInternetConnected = _isInternetConnected.asStateFlow()

    private var _qrCodeResult = MutableStateFlow<String?>("")
    val qrCodeResult = _qrCodeResult.asStateFlow()

    // Configure scanner
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_ALL_FORMATS)
        .enableAllPotentialBarcodes() // Optional
        .build()

    val scanner: BarcodeScanner by lazy { BarcodeScanning.getClient(options) }

    init {
        checkInternetConnection()
    }

    fun checkInternetConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            _isInternetConnected.value = AppUtil.isInternetConnected(context)
        }
    }

    fun processImage(
        imageProxy: ImageProxy?,
        gotoNextScreen: (WifiSSID, WifiPassword, WifiType) -> Unit = { _, _, _ -> },
    ) {
        Log.d("SCANNER", "----------------------")
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
                        Log.d("SCANNER", "ssid: ${ssid}")
                        Log.d("SCANNER", "password: ${password}")
                        Log.d("SCANNER", "type: ${type}")

                        _showToast.value = false
                        imageProxy.close()
                        gotoNextScreen(ssid ?: "", password ?: "", type)
                    } else {
                        Log.d("SCANNER", "Barcode.TYPE_NOT_VALID")
                        _showToast.value = true
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
}