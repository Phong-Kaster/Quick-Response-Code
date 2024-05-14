package com.example.quickresponsecode.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.theme.customizedTextStyle
import com.example.quickresponsecode.util.ViewUtil

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoreTopBar2(
    @DrawableRes iconLeft: Int? = null,
    @DrawableRes iconRight: Int? = null,
    iconLeftColor: Color = Color.Black,
    iconLeftColorBackground: Color = Color.Yellow,
    iconRightColor: Color = Color.Black,
    iconRightColorBackground: Color = Color.Yellow,
    text: String? = stringResource(id = R.string.app_name),
    textColor: Color = Color.Yellow,
    textArrangement: Arrangement.Horizontal = Arrangement.Start,
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(color = Color.Transparent)
            .padding(16.dp)
    ) {
        IconButton(
            onClick = {
                onLeftClick()
            },
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(color = iconLeftColorBackground)
                .size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = iconLeft ?: R.drawable.ic_launcher_foreground),
                contentDescription = null,
                tint = iconLeftColor,
                modifier = Modifier.size(15.dp)
            )
        }



        Row(
            horizontalArrangement = textArrangement,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1F)
        ) {
            Text(
                text = text ?: "",
                maxLines = 1,
                style = customizedTextStyle(
                    fontSize = 16,
                    fontWeight = 700
                ),
                color = textColor,
                modifier = Modifier
                    .basicMarquee(Int.MAX_VALUE)

            )
        }




        if (iconRight != null) {
            IconButton(
                onClick = { onRightClick() },
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(color = iconRightColorBackground)
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRight),
                    contentDescription = null,
                    tint = iconRightColor,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewCoreTopBar2ArrangementLeft() {
    ViewUtil.PreviewContent {
        CoreTopBar2(
            iconLeft = R.drawable.ic_launcher_foreground,
            text = stringResource(id = R.string.app_name)
        )
    }
}

@Preview
@Composable
fun PreviewCoreTopBar2ArrangementCenter() {
    ViewUtil.PreviewContent {
        CoreTopBar2(
            iconLeft = R.drawable.ic_launcher_foreground,
            text = stringResource(id = R.string.app_name),
            textArrangement = Arrangement.Center,
            iconRight = R.drawable.ic_launcher_foreground,
        )
    }
}


@Preview
@Composable
fun PreviewCoreTopBar2ArrangementRight() {
    ViewUtil.PreviewContent {
        CoreTopBar2(
            iconLeft = R.drawable.ic_launcher_foreground,
            text = stringResource(id = R.string.app_name),
            textArrangement = Arrangement.End,
            iconRight = R.drawable.ic_launcher_foreground,
        )
    }
}