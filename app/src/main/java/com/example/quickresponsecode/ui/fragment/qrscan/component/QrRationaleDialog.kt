package com.panda.wifipassword.ui.screen.qr.qrscan.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.quickresponsecode.R

@Composable
fun QrRationaleDialog(
    enable: Boolean,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    if (enable) {
        Dialog(
            onDismissRequest = onDismissRequest,
            content = {
                QrRationaleDialogLayout(
                    onDismissRequest = onDismissRequest,
                    onConfirm = onConfirm
                )
            })
    }
}

@Composable
private fun QrRationaleDialogLayout(
    onDismissRequest: () -> Unit = {},
    onConfirm: ()->Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 10.dp, alignment = Alignment.CenterVertically),
        modifier = Modifier
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(70.dp)
        )

        Text(
            text = stringResource(R.string.next_your_device_may_display_a_dialog_box),
            color = Color.Black,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                fontFamily = FontFamily.Default
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(20.dp))
                .background(color = Color(0xFF1C68FB))
                .clickable {
                    onDismissRequest()
                    onConfirm()
                }
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.got_it),
                color = Color.White,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600)
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewQrRationaleDialog() {
    QrRationaleDialogLayout()
}