package com.example.quickresponsecode.ui.fragment.qrgenerate

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.data.database.model.WifiQr
import com.example.quickresponsecode.data.enums.SecurityLevel
import com.example.quickresponsecode.ui.component.CoreTopBar2
import com.example.quickresponsecode.ui.component.OutlineButton
import com.example.quickresponsecode.ui.component.SolidButton
import com.example.quickresponsecode.util.AppUtil
import com.example.quickresponsecode.util.NavigationUtil.safeNavigateUp
import com.example.quickresponsecode.util.WifiUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrResultFragment : CoreFragment() {

    private val viewModel: QrGenerateViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveUID()
    }

    private fun receiveUID() {
        val uid = arguments?.getLong("uid")
        if (uid == 0L) {
            Toast.makeText(requireContext(), getString(R.string.generate_failed_please_try_again), Toast.LENGTH_SHORT).show()
            safeNavigateUp()
        }

        viewModel.getWifiWithID(uid)
    }

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        QrResultLayout(
            wifiQr = viewModel.wifiQr.collectAsStateWithLifecycle().value,
            onBack = { safeNavigateUp() },
            onDownload = {  },
            onShare = { },
            onCopyToClipboard = { AppUtil.copyToClipboard(context = requireContext(), text = viewModel.wifiQr.value?.wifiPassword ?: "") }
        )
    }
}

@Composable
fun QrResultLayout(
    wifiQr: WifiQr?,
    onBack: () -> Unit = {},
    onDownload: () -> Unit = {},
    onShare: () -> Unit = {},
    onCopyToClipboard: () -> Unit = {},
) {

    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        val rawValue = WifiUtil.generateWifiRawValue(
            ssid = wifiQr?.wifiSSID ?: "",
            password = wifiQr?.wifiPassword ?: "",
            hidden = wifiQr?.hidden ?: false,
            encryption = wifiQr?.securityLevel ?: SecurityLevel.NONE
        )

        bitmap = WifiUtil.encodeAsBitmap(rawValue)
        Log.d("PHONG", "QrResultLayout - raw value: $rawValue")
        Log.d("PHONG", "QrResultLayout - bitmap: $bitmap")
        Log.d("PHONG", "QrResultLayout - bitmap width: ${bitmap?.width} - height: ${bitmap?.height} ")
    }

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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(color = Color.White)
                    .padding(24.dp)
            ) {
                if(bitmap != null){
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(color = Color.Transparent)
                            .padding(vertical = 16.dp)
                            .width(150.dp)
                            .aspectRatio(1F)

                    )
                }

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
                            text = wifiQr?.wifiSSID ?: "",
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
                                text = wifiQr?.wifiPassword ?: "",
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


                SolidButton(
                    modifier = Modifier.fillMaxWidth(),
                    marginHorizontal = 0.dp,
                    onClick = onDownload,
                    backgroundColor = Color(0xFF1C68FB),
                    textColor = Color.White,
                    text = stringResource(id = R.string.download),
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
private fun PreviewQrResult() {
    QrResultLayout(
        wifiQr = WifiQr.fakeWifi()
    )
}