package com.example.quickresponsecode.ui.fragment.qrscan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quickresponsecode.R

@Composable
fun QrTopbar(
    enableFlashlight: Boolean = false,
    modifier: Modifier = Modifier,
    onOpenHistory: () -> Unit = {},
    onOpenFlashlight: () -> Unit = {}
) {
    // HISTORY BUTTON AND FLASHLIGHT
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        IconButton(
            onClick = onOpenHistory,
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(color = Color.Black.copy(alpha = 0.4F))
        )

        IconButton(
            onClick = onOpenFlashlight,
            content = {
                Icon(
                    painter =
                    if (enableFlashlight) painterResource(id = R.drawable.ic_flash_on)
                    else painterResource(id = R.drawable.ic_flash_off),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(color = Color.Black.copy(alpha = 0.4F))
        )
    }
}

@Preview
@Composable
private fun PreviewQrTopbar() {
    Column(modifier = Modifier) {
        QrTopbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .systemBarsPadding()
        )
    }
}