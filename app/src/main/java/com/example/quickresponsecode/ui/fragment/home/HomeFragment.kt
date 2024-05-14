package com.example.quickresponsecode.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.flashlightenhancedversion.lifecycleobserver.CameraPermissionLifecycleObserver
import com.example.jetpack.core.CoreFragment
import com.example.jetpack.core.CoreLayout
import com.example.quickresponsecode.R
import com.example.quickresponsecode.data.enums.CameraNavigationButton
import com.example.quickresponsecode.ui.component.OutlineButton
import com.example.quickresponsecode.ui.fragment.home.component.CameraNavigationButtonLayout
import com.example.quickresponsecode.util.AppUtil.getCameraProvider
import com.example.quickresponsecode.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : CoreFragment() {

    private val viewModel: HomeViewModel by viewModels()
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

    private val settingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    override fun onResume() {
        super.onResume()
        viewModel.checkInternetConnection()
    }

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        var chosenButton by remember { mutableStateOf(CameraNavigationButton.Scan) }



        HomeLayout(
            isInternetConnected = viewModel.isInternetConnected.collectAsState().value,
            showToast = viewModel.showToast.collectAsState().value,
            chosenButton = chosenButton,
            onClickCameraNavigationButton = {
                chosenButton = it
            },
            onReturnImageProxy = { imageProxy: ImageProxy ->
                viewModel.processImage(imageProxy = imageProxy)
            },
            onOpenWifiSettings = {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                settingLauncher.launch(intent)
            }
        )
    }
}

@Composable
fun HomeLayout(
    isInternetConnected: Boolean = true,
    showToast: Boolean = true,
    chosenButton: CameraNavigationButton = CameraNavigationButton.Scan,
    onClickCameraNavigationButton: (CameraNavigationButton) -> Unit = {},
    onReturnImageProxy: (ImageProxy) -> Unit = {},
    onOpenWifiSettings: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var enableFlashlight by remember { mutableStateOf(false) }


    /** Configure Camera X Preview */
    val previewView = remember { PreviewView(context) }
    var camera: Camera? by remember { mutableStateOf(null) }
    val cameraSelector = remember {
        CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }


    /** Configure Image Analysis */
    val executor = ContextCompat.getMainExecutor(context)
    val resolutionSelector: ResolutionSelector = remember {
        ResolutionSelector
            .Builder()
            .setResolutionStrategy(
                ResolutionStrategy(
                    Size(1280, 720),
                    ResolutionStrategy.FALLBACK_RULE_NONE
                )
            )
            .build()
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setResolutionSelector(resolutionSelector)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    LaunchedEffect(
        key1 = enableFlashlight,
        block = { camera?.cameraControl?.enableTorch(enableFlashlight) }
    )

    LaunchedEffect(Unit) {
        try {
            val cameraProvider = context.getCameraProvider()
            val preview = androidx.camera.core.Preview.Builder().build()

            imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy ->
                onReturnImageProxy(imageProxy)
            })


            cameraProvider.unbindAll()
            val bindingCamera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
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


                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)
                        .align(Alignment.BottomCenter)
                ) {

                    // The QR code scanned is invalid
                    AnimatedVisibility(
                        visible = showToast,
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(50.dp))
                                    .background(color = Color(0xFFFDF6E9))
                                    .padding(vertical = 16.dp, horizontal = 16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_warning),
                                    contentDescription = null,
                                    tint = Color(0xFFEEA720),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.the_qr_code_scanned_is_invalid),
                                    color = Color(0xFF333333),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(0.7F)
                                )
                                /*OutlineButton(
                                    text = "Connect",
                                    textColor = Color(0xFFF1B94D),
                                    borderStroke = BorderStroke(
                                        width = 1.dp,
                                        color = Color(0xFFF1B94D)
                                    ),
                                    paddingVertical = 10.dp,
                                    paddingHorizontal = 10.dp,
                                    onClick = {}
                                )*/
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = isInternetConnected == false,
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(50.dp))
                                    .background(color = Color(0xFFFDF6E9))
                                    .padding(vertical = 16.dp, horizontal = 16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_warning),
                                    contentDescription = null,
                                    tint = Color(0xFFEEA720),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.turn_on_wi_fi_for_effective_use),
                                    color = Color(0xFF333333),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(0.7F)
                                )
                                OutlineButton(
                                    text = stringResource(id = R.string.connect),
                                    textColor = Color(0xFFF1B94D),
                                    borderStroke = BorderStroke(
                                        width = 1.dp,
                                        color = Color(0xFFF1B94D)
                                    ),
                                    marginHorizontal = 0.dp,
                                    marginVertical = 0.dp,
                                    paddingVertical = 5.dp,
                                    paddingHorizontal = 10.dp,
                                    onClick = onOpenWifiSettings
                                )
                            }
                        }
                    )


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()

                            .clip(shape = RoundedCornerShape(50.dp))
                            .background(color = Color.Black)
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                    ) {
                        CameraNavigationButton.entries.forEach {
                            CameraNavigationButtonLayout(
                                modifier = Modifier.weight(1f),
                                enable = chosenButton == it,
                                icon = it.icon,
                                text = it.text,
                                onClick = {
                                    onClickCameraNavigationButton(it)
                                }
                            )

                            /*if (it != CameraNavigationButton.Import) {
                                VerticalDivider(
                                    modifier = Modifier
                                        .height(20.dp)
                                        .padding(horizontal = 3.dp)
                                        .background(color = Color.White.copy(alpha = 0.75F))
                                )
                            }*/
                        }
                    }
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