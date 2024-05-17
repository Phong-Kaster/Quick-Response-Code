package com.example.quickresponsecode.ui.fragment.qrhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickresponsecode.data.database.model.WifiQr
import com.example.quickresponsecode.data.repository.WifiQrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrHistoryViewModel
@Inject
constructor(
    private val repository: WifiQrRepository
) : ViewModel(){

    private val _records = MutableStateFlow<List<WifiQr>>(emptyList())
    val records = _records.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO){
            repository.getAllFlow().collectLatest {
                _records.value = it
            }
        }
    }
}