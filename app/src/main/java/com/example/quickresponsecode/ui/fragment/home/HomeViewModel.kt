package com.example.quickresponsecode.ui.fragment.home

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.example.quickresponsecode.util.ScannerUtil
import com.example.quickresponsecode.util.ScannerUtil.toInputImage
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel
@Inject
constructor() : ViewModel() {

    private var _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog = _showDialog.asStateFlow()

    private var _qrCodeResult = MutableStateFlow<String>("")
    val qrCodeResult = _qrCodeResult.asStateFlow()

    // Configure scanner
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_ALL_FORMATS)
        .enableAllPotentialBarcodes() // Optional
        .build()

    val scanner: BarcodeScanner by lazy { BarcodeScanning.getClient(options) }

    fun processImage(imageProxy: ImageProxy?) {
        if (imageProxy == null) return

        val inputImage = imageProxy.toInputImage()
        if (inputImage == null) {
            imageProxy.close()
            return
        }

        scanner
            .process(inputImage)
            .addOnSuccessListener { barcodes ->
                Log.d("SCANNER", "Home View Model - addOnSuccessListener")

                for (barcode in barcodes) {
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints

                    val rawValue = barcode.rawValue

                    val valueType: Int = barcode.valueType


                    _qrCodeResult.value = ScannerUtil.getQuickResponseCodeResult(barcode = barcode, valueType = valueType)
                    _showDialog.value = true
                }
            }
            .addOnFailureListener { exception: Exception ->
                Log.d("SCANNER", "Home View Model - addOnFailureListener")
                imageProxy.close()
                exception.printStackTrace()
            }
    }

    fun dismissDialog(){
        _showDialog.value = false
        _qrCodeResult.value = ""

    }
}