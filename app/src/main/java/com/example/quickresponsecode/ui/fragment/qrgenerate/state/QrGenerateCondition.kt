package com.example.quickresponsecode.ui.fragment.qrgenerate.state

sealed class QrGenerateCondition
constructor() {
    data object None : QrGenerateCondition()
    data object Success : QrGenerateCondition()
    data object Failure : QrGenerateCondition()
}