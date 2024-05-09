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

    fun getQuickResponseCodeResult(barcode: Barcode, valueType: Int): String {
        // See API reference for complete list of supported types
        when (valueType) {
            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi!!.ssid
                val password = barcode.wifi!!.password
                val type = barcode.wifi!!.encryptionType

                Log.d("SCANNER", "---------------------------")
                Log.d("SCANNER", "Barcode.TYPE_WIFI")
                Log.d("SCANNER", "ssid: ${ssid}")
                Log.d("SCANNER", "password: ${password}")
                Log.d("SCANNER", "type: ${type}")

                return "ssid $ssid password $password type $type"
            }



            Barcode.TYPE_URL -> {
                val title = barcode.url!!.title
                val url = barcode.url!!.url

                Log.d("SCANNER", "---------------------------")
                Log.d("SCANNER", "Barcode.TYPE_URL")
                Log.d("SCANNER", "title: ${title}")
                Log.d("SCANNER", "url: ${url}")

                return "title $title $url"
            }
        }

        return ""
    }
}