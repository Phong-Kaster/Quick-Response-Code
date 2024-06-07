package com.example.quickresponsecode.ui.fragment.qrscan

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.example.quickresponsecode.util.AppUtil
import com.example.quickresponsecode.util.NavigationUtil.safeNavigateUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrSuccessFragment : CoreFragment() {

    private var ssid: String? by mutableStateOf(null)
    private var password: String? by mutableStateOf(null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getWifiInfo()
    }

    private fun getWifiInfo() {
        ssid = arguments?.getString("wifiSSID")
        password = arguments?.getString("wifiPassword")
    }

    @Composable
    override fun ComposeView() {
        QrSuccessLayout(
            wifiSSID = ssid ?: "",
            wifiPassword = password ?: "",
            onShare = {

            },
            onCopyToClipboard = { AppUtil.copyToClipboard(context = requireContext(), text = password ?: "") },
            onBack = {
                safeNavigateUp()
            }
        )
    }
}

@Composable
private fun QrSuccessLayout(
    wifiSSID: String,
    wifiPassword: String,
    onBack: () -> Unit = {},
    onShare: () -> Unit = {},
    onCopyToClipboard: () -> Unit = {},
) {
    CoreLayout(
        backgroundColor = Color.White,
        topBar = {
            CoreTopBar2(
                text = stringResource(R.string.connect_wifi),
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
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF004BDC),
                    Color(0xFF8DB4FF),
                ),
            )
        ),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(24.dp)
            ) {
                // QR EXAMPLE PHOTO
                Image(
                    painter = painterResource(id = R.drawable.ic_success),
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .aspectRatio(1F)
                )

                Text(
                    text = stringResource(R.string.successfully),
                    color = Color(0xFF08C274),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(500)
                    ),
                    modifier = Modifier
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
                            text = wifiSSID,
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                        ) {
                            Text(
                                text = wifiPassword,
                                style = TextStyle(
                                    color = Color(0xFF333333),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(500)
                                )
                            )


                            Icon(
                                painter = painterResource(id = R.drawable.ic_copy_to_clipboard),
                                contentDescription = null,
                                tint = Color(0xFF6B6B6B),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(15.dp))
                                    .clickable { onCopyToClipboard() }
                            )
                        }
                    }
                }



                OutlineButton(
                    text = stringResource(R.string.share_with_my_community),
                    modifier = Modifier.fillMaxWidth(),
                    marginHorizontal = 0.dp,
                    marginVertical = 0.dp,
                    borderStroke = BorderStroke(width = 1.dp, color = Color(0xFF1C68FB)),
                    onClick = onShare,
                    backgroundColor = Color.White,
                    textColor = Color(0xFF1C68FB),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600)
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
            }
        }
    )
}

@Preview
@Composable
private fun PreviewQrSuccess() {
    QrSuccessLayout(
        wifiSSID = "Example",
        wifiPassword = "123465789"
    )
}