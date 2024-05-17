package com.example.quickresponsecode.ui.fragment.qrhistory.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickresponsecode.R
import com.example.quickresponsecode.data.database.model.WifiQr
import com.example.quickresponsecode.util.LocalDateUtil.toDateWithPattern

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QrHistoryLayoutForData(
    records: List<WifiQr>,
    onClick: (Long)->Unit = {}
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(
            items = records,
            key = { it.id },
            itemContent =
            { wifiQr ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onClick(wifiQr.id)
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_qr_example_2),
                            contentDescription = null,
                            modifier = Modifier
                                .width(64.dp)
                                .aspectRatio(1F)
                        )

                        Column(modifier = Modifier.wrapContentHeight()) {
                            Text(
                                text = wifiQr.wifiSSID,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(500)
                                ),
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            Text(
                                text = wifiQr.epochDay.toDateWithPattern(pattern = "MMM dd, yyyy"),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF707070)
                                ),
                                maxLines = 1,
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                    )
                }

            })
    }
}

@Preview
@Composable
private fun PreviewQrHistoryLayoutForData() {
    QrHistoryLayoutForData(
        records = listOf(WifiQr.fakeWifi())
    )
}