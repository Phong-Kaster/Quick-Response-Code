package com.example.quickresponsecode.ui.fragment.qrgenerate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.fragment.qrgenerate.state.QrGenerateState

@Composable
fun WifiHidden(
    qrGenerateState: QrGenerateState,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.hidden),
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
                .clip(shape = RoundedCornerShape(25.dp))
                .clickable { expanded = true }
                .background(color = Color.White)
                .padding(vertical = 16.dp, horizontal = 16.dp),
        ) {
            Text(
                text =
                if (qrGenerateState.hidden) stringResource(id = R.string.yes)
                else stringResource(id = R.string.no),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400)
                ),
                modifier = Modifier.weight(0.9F)
            )

            Column(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .clickable { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF5B5B5B)
                )

                DropdownMenu(
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(horizontal = 25.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White),
                        text = {
                            Text(
                                text = stringResource(R.string.yes),
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(400)
                                )
                            )
                        },
                        onClick = {
                            expanded = false
                            qrGenerateState.hidden = true
                        }
                    )

                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White),
                        text = {
                            Text(
                                text = stringResource(R.string.no),
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(400)
                                )
                            )
                        },
                        onClick = {
                            expanded = false
                            qrGenerateState.hidden = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewWifiHidden() {
    WifiHidden(
        qrGenerateState = QrGenerateState(),
        modifier = Modifier.fillMaxWidth()
    )
}