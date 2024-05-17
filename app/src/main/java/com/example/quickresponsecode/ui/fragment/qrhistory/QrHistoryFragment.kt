package com.example.quickresponsecode.ui.fragment.qrhistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.data.database.model.WifiQr
import com.example.quickresponsecode.data.enums.Method
import com.example.quickresponsecode.ui.component.CoreTopBar2
import com.example.quickresponsecode.ui.fragment.qrhistory.component.QrHistoryLayoutForData
import com.example.quickresponsecode.util.NavigationUtil.safeNavigate
import com.example.quickresponsecode.util.NavigationUtil.safeNavigateUp
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QrHistoryFragment : CoreFragment() {

    private val viewModel: QrHistoryViewModel by viewModels()

    @Composable
    override fun ComposeView() {
        super.ComposeView()

        var chosenMethod by remember { mutableStateOf(com.example.quickresponsecode.data.enums.Method.Scan) }
        val records = viewModel.records.collectAsStateWithLifecycle().value
        val filterRecords by remember(records){
            derivedStateOf {
                if (chosenMethod == Method.Generate) {
                    records.sortedByDescending { it.epochDay }.filter { it.method == Method.Generate}
                } else {
                    records.sortedByDescending { it.epochDay }.filter { it.method == Method.Scan }
                }
            }
        }

        QrHistoryLayout(
            chosenMethod = chosenMethod,
            records = filterRecords,
            onBack = { safeNavigateUp() },
            onClick = { uid ->
                val destination = R.id.toQrResult
                safeNavigate(
                    destination = destination,
                    bundle = bundleOf("uid" to uid),
                    navOptions = NavOptions.Builder().setPopUpTo(R.id.qrGenerateFragment, true)
                        .build()
                )
            },
            onChangeMethod = { chosenMethod = it }
        )
    }
}

@Composable
fun QrHistoryLayout(
    chosenMethod: Method,
    records: List<WifiQr>,
    onBack: () -> Unit = {},
    onClick: (Long) -> Unit = {},
    onChangeMethod: (Method) -> Unit = {}
) {


    CoreLayout(
        topBar = {
            CoreTopBar2(
                text = stringResource(R.string.history),
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
            ) {
                /**
                 * BUTTON GENERATE AND SCAN*/
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(25.dp))
                        .background(color = Color(0xFFE8F0FF), shape = RoundedCornerShape(25.dp))

                ) {
                    // button scan
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1F)
                            .clip(shape = RoundedCornerShape(25.dp))
                            .clickable {
                                onChangeMethod(Method.Scan)
                            }
                            .background(
                                color = if (chosenMethod == Method.Scan) Color(0xFF1C68FB) else Color.Transparent
                            )
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(id = R.string.scan),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(400)
                            ),
                            color = if (chosenMethod == Method.Scan) Color.White else Color.Black,
                            modifier = Modifier
                        )
                    }

                    // button generate
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1F)
                            .clip(shape = RoundedCornerShape(25.dp))
                            .clickable {
                                onChangeMethod(Method.Generate)
                            }
                            .background(
                                color = if (chosenMethod == Method.Generate) Color(0xFF1C68FB) else Color.Transparent
                            )
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(id = R.string.generate),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(400)
                            ),
                            color = if (chosenMethod == Method.Generate) Color.White else Color.Black,
                            modifier = Modifier
                        )
                    }
                }

                if (records.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(space = 15.dp, Alignment.CenterVertically),
                        modifier = Modifier.fillMaxSize()){
                        Image(
                            painter = painterResource(id = R.drawable.img_no_data),
                            contentDescription = null,
                        )
                        
                        Text(text = stringResource(R.string.no_data),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
                            )
                        )
                    }

                } else {
                    QrHistoryLayoutForData(records = records, onClick = onClick)
                }

            }
        }
    )
}

@Preview
@Composable
private fun PreviewQrHistory() {
    QrHistoryLayout(
        chosenMethod = Method.Scan,
        records = listOf(WifiQr.fakeWifi(),)
    )
}

@Preview
@Composable
private fun PreviewQrHistoryForEmpty() {
    QrHistoryLayout(
        chosenMethod = Method.Generate,
        records = listOf())
}