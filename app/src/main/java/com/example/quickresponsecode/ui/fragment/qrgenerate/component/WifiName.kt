package com.example.quickresponsecode.ui.fragment.qrgenerate.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.fragment.qrgenerate.QrGenerateState

@Composable
fun WifiName(
    modifier: Modifier = Modifier,
    qrGenerateState: QrGenerateState
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
                        text = stringResource(R.string.enter_network_name),
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
}

@Preview
@Composable
fun PreviewWifiName() {
    WifiName(
        qrGenerateState = QrGenerateState(),
        modifier = Modifier
    )
}