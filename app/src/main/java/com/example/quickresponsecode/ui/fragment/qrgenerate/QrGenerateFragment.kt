package com.example.quickresponsecode.ui.fragment.qrgenerate

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.example.quickresponsecode.ui.fragment.qrgenerate.state.QrGenerateState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrGenerateFragment : CoreFragment() {

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        QrGenerateLayout(
            qrGenerateState = QrGenerateState()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QrGenerateLayout(
    qrGenerateState: QrGenerateState,
    onBack: () -> Unit = {}
) {
    CoreLayout(
        topBar = {
            CoreTopBar2(
                text = stringResource(id = R.string.generate),
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
                onClick = { },
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(color = Color.White)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.network_name),
                    style = TextStyle(
                        fontWeight = FontWeight(400),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicTextField(
                    value = qrGenerateState.name,
                    onValueChange = { qrGenerateState.name = it },
                    cursorBrush = SolidColor(Color.Cyan),
                    singleLine = true,
                    /*lineLimits = TextFieldLineLimits.SingleLine,*/
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400)
                    ),
                    decorationBox = { innerTextField ->
                        if (qrGenerateState.name.isEmpty()) {
                            Text(
                                text = "Enter network name",
                                style = TextStyle(
                                    color = Color(0xFFB0B0B0),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(400)
                                )
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE1E1E1),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(vertical = 16.dp, horizontal = 16.dp),
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