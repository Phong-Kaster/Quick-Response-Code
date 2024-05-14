package com.example.quickresponsecode.util

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

object ScannerUtil {

    @OptIn(ExperimentalGetImage::class)
    fun ImageProxy.toInputImage() =
        if (this.image == null) {
            null
        } else {
            InputImage.fromMediaImage(this.image!!, this.imageInfo.rotationDegrees)
        }

    fun getQuickResponseCodeResult(barcode: Barcode, valueType: Int): String? {
        // See API reference for complete list of supported types
        return if (Barcode.TYPE_WIFI == valueType) {
            val ssid = barcode.wifi!!.ssid
            val password = barcode.wifi!!.password
            val type = barcode.wifi!!.encryptionType

            Log.d("SCANNER", "---------------------------")
            Log.d("SCANNER", "Barcode.TYPE_WIFI")
            Log.d("SCANNER", "ssid: ${ssid}")
            Log.d("SCANNER", "password: ${password}")
            Log.d("SCANNER", "type: ${type}")

            return "ssid $ssid password $password type $type"
        } else null
    }
}