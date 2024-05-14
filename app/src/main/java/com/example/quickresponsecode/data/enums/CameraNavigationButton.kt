package com.example.quickresponsecode.data.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.quickresponsecode.R

@Immutable
enum class CameraNavigationButton(
    @StringRes val text: Int,
    @DrawableRes val icon: Int
) {
    Scan(icon = R.drawable.ic_scan, text = R.string.scan),
    Generate(icon = R.drawable.ic_generate, text = R.string.generate),
    Import(icon = R.drawable.ic_import, text = R.string.import_lowercase),

    ;
}