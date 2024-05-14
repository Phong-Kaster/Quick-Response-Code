package com.example.quickresponsecode.ui.fragment.qr

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.component.CoreTopBar2
import com.example.quickresponsecode.ui.component.OutlineButton
import com.example.quickresponsecode.ui.component.SolidButton
import com.example.quickresponsecode.util.NavigationUtil.safeNavigateUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrReadFragment : CoreFragment() {

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        QrReadLayout(
            onBack = { safeNavigateUp() },
            wifiName = "Example",
            wifiPassword = "12345678"
        )
    }
}

@Composable
fun QrReadLayout(
    onBack: () -> Unit = {},
    wifiName: String,
    wifiPassword: String,
) {
    CoreLayout(
        topBar = {
            CoreTopBar2(
                text = stringResource(R.string.result),
                textColor = Color.White,
                textArrangement = Arrangement.Center,
                iconLeft = R.drawable.ic_back,
                iconLeftColor = Color.White,
                iconLeftColorBackground = Color.Transparent,
                onLeftClick = onBack,
                onRightClick = {},
                iconRight = R.drawable.ic_back,
                iconRightColor = Color.Transparent,
                iconRightColorBackground = Color.Transparent,
            )
        },
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF004BDC),
                Color(0xFF8DB4FF),
            ),
        ),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(color = Color.White)
                    .padding(24.dp)
            ) {
                // QR EXAMPLE PHOTO
                Image(
                    painter = painterResource(id = R.drawable.img_qr_example),
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .aspectRatio(1F)
                )


                // WIFI NAME & PASSWORD
                Column(
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xFFF4F4F4))
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.name),
                            style = TextStyle(
                                color = Color(0xFF8A8A8A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400)
                            )
                        )

                        Text(
                            text = wifiName,
                            style = TextStyle(
                                color = Color(0xFF333333),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500)
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.password),
                            style = TextStyle(
                                color = Color(0xFF8A8A8A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400)
                            )
                        )

                        Text(
                            text = wifiPassword,
                            style = TextStyle(
                                color = Color(0xFF333333),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500)
                            )
                        )
                    }
                }


                SolidButton(
                    modifier = Modifier.fillMaxWidth(),
                    marginHorizontal = 0.dp,
                    onClick = {},
                    backgroundColor = Color(0xFF1C68FB),
                    textColor = Color.White,
                    text = stringResource(id = R.string.connect),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600)
                    ),
                    shape = RoundedCornerShape(25.dp)
                )

                OutlineButton(
                    text = stringResource(R.string.share_with_my_community),
                    modifier = Modifier.fillMaxWidth(),
                    marginHorizontal = 0.dp,
                    marginVertical = 0.dp,
                    borderStroke = BorderStroke(width = 1.dp, color = Color(0xFF1C68FB)),
                    onClick = {},
                    backgroundColor = Color.White,
                    textColor = Color(0xFF1C68FB),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600)
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
            }
        })
}

@Preview
@Composable
private fun PreviewQrRead() {
    QrReadLayout(
        wifiName = "Wifi Name",
        wifiPassword = "Wifi Password",
    )
}