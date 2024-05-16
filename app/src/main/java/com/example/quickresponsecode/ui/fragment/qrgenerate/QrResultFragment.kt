package com.example.quickresponsecode.ui.fragment.qrgenerate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.ui.component.CoreTopBar2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrResultFragment : CoreFragment() {

    private val viewModel : QrGenerateViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = arguments?.getLong("uid")
        showToast("uid: $uid !")
    }

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        QrResultLayout()
    }
}

@Composable
fun QrResultLayout(
    onBack: ()->Unit = {},
) {
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .background(color = Color.White)
                    .padding(24.dp)
            ) {

            }
        }
    )
}

@Preview
@Composable
private fun PreviewQrResult() {
    QrResultLayout()
}