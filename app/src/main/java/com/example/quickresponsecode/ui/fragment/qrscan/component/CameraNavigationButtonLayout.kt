package com.panda.wifipassword.ui.screen.qr.qrscan.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickresponsecode.R

@Composable
fun CameraNavigationButtonLayout(
    modifier:Modifier = Modifier,
    enable: Boolean = true,
    @DrawableRes icon: Int = R.drawable.ic_scan,
    @StringRes text: Int = R.string.app_name,
    onClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .width(120.dp)
            .clip(shape = RoundedCornerShape(50.dp))
            .clickable {
                onClick()
            }
            .background(
                color = if (enable) Color(0xFF3D3A36) else Color.Transparent,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 0.dp, vertical = 10.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = stringResource(id = text),
            color = Color.White,
            maxLines = 1,
            style = TextStyle(fontSize = 12.sp),
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun PreviewCameraNavigationButton() {
    Column(modifier = Modifier.background(color = Color.Black.copy(alpha = 0.7F))) {
        CameraNavigationButtonLayout(
            icon = R.drawable.ic_scan,
            text = R.string.generate,
            enable = true
        )
    }

}