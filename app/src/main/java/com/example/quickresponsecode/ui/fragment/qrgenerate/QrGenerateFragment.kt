package com.example.quickresponsecode.ui.fragment.qrgenerate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.component.CoreTopBar2
import com.example.quickresponsecode.ui.fragment.qrgenerate.state.QrGenerateCondition
import com.example.quickresponsecode.ui.fragment.qrgenerate.state.QrGenerateState
import com.example.quickresponsecode.util.NavigationUtil.safeNavigate
import com.example.quickresponsecode.util.NavigationUtil.safeNavigateUp
import com.example.quickresponsecode.util.collectLatestOnResume
import com.panda.wifipassword.ui.screen.qr.qrgenerate.component.WifiHidden
import com.panda.wifipassword.ui.screen.qr.qrgenerate.component.WifiName
import com.panda.wifipassword.ui.screen.qr.qrgenerate.component.WifiPassword
import com.panda.wifipassword.ui.screen.qr.qrgenerate.component.WifiSecurityLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrGenerateFragment : CoreFragment() {

    private val viewModel: QrGenerateViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenCondition()
    }

    private fun listenCondition() {
        collectLatestOnResume(viewModel.condition) { condition ->
            when (condition) {
                QrGenerateCondition.Success -> {
                    val uid = viewModel.uid.value
                    val destination = R.id.toQrResult
                    safeNavigate(
                        destination = destination,
                        bundle = bundleOf("uid" to uid),
                        navOptions = NavOptions.Builder().setPopUpTo(R.id.qrGenerateFragment, true).build()
                    )
                }
                QrGenerateCondition.Failure -> Toast.makeText(requireContext(), "Failed, please try again !", Toast.LENGTH_SHORT).show()
                QrGenerateCondition.None -> {}
            }
        }
    }

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        QrGenerateLayout(
            qrGenerateState = viewModel.qrGenerateState,
            onGenerate = { viewModel.generate() },
            onBack = {safeNavigateUp()}
        )
    }
}

@Composable
fun QrGenerateLayout(
    qrGenerateState: QrGenerateState,
    onBack: () -> Unit = {},
    onGenerate: () -> Unit = {},
) {
    val context = LocalContext.current

    CoreLayout(
        topBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF004BDC))
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .statusBarsPadding()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .background(color = Color.Transparent)
                        .size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Text(
                    text = stringResource(id = R.string.generate),
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight(600)
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            Button(
                colors = ButtonColors(
                    containerColor = Color(0xFF004BDC),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    if (qrGenerateState.ssid.isEmpty() || qrGenerateState.password.isEmpty()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_fill_all_fields),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onGenerate()
                    }
                },
                content = {
                    Text(
                        text = stringResource(id = R.string.generate),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(600)
                        ),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                })
        },
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF004BDC),
                Color(0xFF8DB4FF),
            ),
        ),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(color = Color.White)
                    .padding(24.dp)
            ) {

                WifiName(
                    qrGenerateState = qrGenerateState,
                    modifier = Modifier.fillMaxWidth()
                )

                WifiPassword(
                    qrGenerateState = qrGenerateState,
                    modifier = Modifier.fillMaxWidth()
                )

                WifiSecurityLevel(
                    qrGenerateState = qrGenerateState,
                    modifier = Modifier.fillMaxWidth()
                )

                WifiHidden(
                    qrGenerateState = qrGenerateState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        })
}

@Preview
@Composable
private fun PreviewQrGenerate() {
    QrGenerateLayout(
        qrGenerateState = QrGenerateState()
    )
}