package com.example.quickresponsecode.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.flashlightenhancedversion.lifecycleobserver.CameraPermissionLifecycleObserver
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.util.AppUtil.getCameraProvider
import com.example.quickresponsecode.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : CoreFragment() {

    private lateinit var cameraPermissionObserver: CameraPermissionLifecycleObserver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserverCameraPermission()
        requestCameraPermission()
    }

    /*************************************************
     * for request camera permission
     */
    private var showPopupOnePermission: Boolean by mutableStateOf(false)
    private val callbackOnePermission = object : CameraPermissionLifecycleObserver.Callback {
        override fun openRationaleDialog() {
            showPopupOnePermission = true
        }
    }

    private fun setupObserverCameraPermission() {
        cameraPermissionObserver = CameraPermissionLifecycleObserver(
            registry = requireActivity().activityResultRegistry,
            activity = requireActivity(),
            callback = callbackOnePermission
        )
        lifecycle.addObserver(cameraPermissionObserver)
    }


    /*************************************************
     * for using Camera X
     */
    private fun requestCameraPermission() {
        val isAccessed: Boolean = PermissionUtil.isCameraAccessible(context = requireContext())
        if (isAccessed) return
        cameraPermissionObserver.launcher.launch(android.Manifest.permission.CAMERA)
    }

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        HomeLayout(
            onOpenGallery = {
                showToast("Open Gallery")
            }
        )
    }
}

@Composable
fun HomeLayout(
    onOpenGallery: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }


    var enableFlashlight by remember { mutableStateOf(false) }
    val cameraSelector = remember {
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }
    var camera: Camera? by remember { mutableStateOf(null) }


    LaunchedEffect(
        key1 = enableFlashlight,
        block = { camera?.cameraControl?.enableTorch(enableFlashlight) }
    )

    LaunchedEffect(Unit) {
        try {
            val cameraProvider = context.getCameraProvider()
            val preview = androidx.camera.core.Preview.Builder().build()


            cameraProvider.unbindAll()
            val bindingCamera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )

            camera = bindingCamera
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    CoreLayout(
        backgroundColor = Color.DarkGray,
        content = {
            Box(
                modifier = Modifier
            ) {
                if (LocalInspectionMode.current) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(0xFFB6B2AF))
                    )
                } else {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_scan_overlay),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .aspectRatio(1f)
                        .align(BiasAlignment(0f, -0.2f)),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.End
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_image),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                            .clickable(
                                enabled = true,
                                onClick = onOpenGallery
                            )
                            .background(color = Color(0x66000000))
                            .padding(16.dp)
                    )

                    Image(
                        painter = painterResource(
                            id = if (enableFlashlight) R.drawable.ic_flash_on
                            else R.drawable.ic_flash_off
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                            .clickable(
                                enabled = true,
                                onClick = {
                                    enableFlashlight = !enableFlashlight
                                })
                            .background(color = Color(0x66000000))
                            .padding(12.dp)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewHome() {
    HomeLayout()
}