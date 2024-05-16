package com.example.quickresponsecode.ui.fragment.qrgenerate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.fragment.qrgenerate.state.QrGenerateState

@Composable
fun WifiPassword(
    qrGenerateState: QrGenerateState,
    modifier: Modifier = Modifier
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.password),
            style = TextStyle(
                fontWeight = FontWeight(400),
                fontSize = 14.sp
            ),
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFE1E1E1),
                    shape = RoundedCornerShape(25.dp)
                )
        ) {
            BasicTextField(
                value = qrGenerateState.password,
                onValueChange = { qrGenerateState.password = it },
                cursorBrush = SolidColor(Color.Cyan),
                singleLine = true,
                /*lineLimits = TextFieldLineLimits.SingleLine,*/
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400)
                ),
                decorationBox = { innerTextField ->
                    if (qrGenerateState.password.isEmpty()) {
                        Text(
                            text = "Enter password",
                            style = TextStyle(
                                color = Color(0xFFB0B0B0),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400)
                            )
                        )
                    }
                    innerTextField()
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .weight(0.9f)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
            )

            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                content = {
                    Icon(
                        painter = if (passwordVisible) painterResource(id = R.drawable.ic_visibility)
                        else painterResource(id = R.drawable.ic_visibility_off),
                        contentDescription = null,
                        tint = Color(0xFF5B5B5B)
                    )
                })
        }

    }
}

@Preview
@Composable
private fun PreviewWifiPassword() {
    WifiPassword(
        qrGenerateState = QrGenerateState(),
        modifier = Modifier.background(color = Color.White)
    )
}