package com.example.quickresponsecode.ui.fragment

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : CoreFragment() {

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        HomeLayout()
    }
}

@Composable
fun HomeLayout() {
    CoreLayout(
        backgroundColor = Color.DarkGray,
        content = {

        }
    )
}

@Preview
@Composable
private fun PreviewHome() {
    HomeLayout()
}