package com.example.quickresponsecode.ui.fragment.qrgenerate

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrGenerateViewModel
@Inject
constructor() : ViewModel()
{
    val qrGenerateState = QrGenerateState()

    fun generate(){
        Log.d("PHONG", "--------")
        Log.d("PHONG", "name: ${qrGenerateState.name}")
        Log.d("PHONG", "password :${qrGenerateState.password}")
        Log.d("PHONG", "isHidden :${qrGenerateState.isHidden}")
        Log.d("PHONG", "securityLevel :${qrGenerateState.securityLevel}")
    }
}