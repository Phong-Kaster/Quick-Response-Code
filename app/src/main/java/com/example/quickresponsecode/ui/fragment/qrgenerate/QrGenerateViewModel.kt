package com.example.quickresponsecode.ui.fragment.qrgenerate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickresponsecode.data.database.model.WifiQr
import com.example.quickresponsecode.data.enums.Method
import com.example.quickresponsecode.data.repository.WifiQrRepository
import com.example.quickresponsecode.ui.fragment.qrgenerate.state.QrGenerateCondition
import com.example.quickresponsecode.ui.fragment.qrgenerate.state.QrGenerateState
import com.example.quickresponsecode.util.LocalDateUtil.toElapsedMinutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class QrGenerateViewModel
@Inject
constructor(
    private val wifiQrRepository: WifiQrRepository
) : ViewModel() {
    val qrGenerateState = QrGenerateState()

    private val _uid = MutableStateFlow<Long>(0)
    val uid = _uid.asStateFlow()

    private val _condition = MutableStateFlow<QrGenerateCondition>(QrGenerateCondition.None)
    val condition = _condition.asStateFlow()

    private val _wifiQr = MutableStateFlow<WifiQr?>(null)
    val wifiQr = _wifiQr.asStateFlow()

    fun generate(
        onShowToast: ()->Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            if(qrGenerateState.ssid.isEmpty() || qrGenerateState.password.isEmpty()){
                onShowToast()
                return@launch
            }

            val wifiQr = WifiQr(
                id = 0,
                wifiSSID = qrGenerateState.ssid,
                wifiPassword = qrGenerateState.password,
                hidden = qrGenerateState.hidden,
                method = Method.Generate,
                securityLevel = qrGenerateState.securityLevel,
                epochDay = LocalDate.now().toEpochDay(),
                epochMinutes = LocalDateTime.now().toElapsedMinutes(),
            )


            try {
                val id = wifiQrRepository.insertOrUpdate(wifiQr)
                _uid.value = id
                _condition.value = QrGenerateCondition.Success
            } catch (ex: Exception) {
                ex.printStackTrace()
                _condition.value = QrGenerateCondition.Failure
            }
        }
    }

    fun getWifiWithID(uid: Long?){
        if(uid == null) return

        viewModelScope.launch(Dispatchers.IO){
            _wifiQr.value = wifiQrRepository.findWithID(uid)
        }
    }
}