package com.example.flashlightenhancedversion.lifecycleobserver

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.quickresponsecode.R
import com.example.quickresponsecode.util.PermissionUtil
import javax.inject.Inject

typealias PermissionName = String
/**
 * @author Phong-Kaster
 *
 * This class encapsulates the logic for requesting notification runtime permissions.
 * It manages the permission request process and handles the user's response within its own scope,
 * rather than relying on direct definitions within an Activity or Fragment.
 *
 * This class is similar to the Notification Runtime Launcher, but instead of using the standard Android alert dialog,
 * it displays a custom Jetpack Compose rationale dialog when requesting notification permissions.
 * To achieve this, it uses an interface to control relevant boolean variables.
 *
 * @see [Receive an activity result in a separate class] https://developer.android.com/training/basics/intents/result#separate
 */
class CameraPermissionLifecycleObserver
@Inject
constructor(
    private val registry: ActivityResultRegistry,
    private val activity: Activity,
    private val callback: Callback
) : DefaultLifecycleObserver {
    lateinit var launcher: ActivityResultLauncher<PermissionName>
    lateinit var settingLauncher: ActivityResultLauncher<Intent>

    private val tag = "NotificationRuntimeLauncher2"
    override fun onCreate(owner: LifecycleOwner) {
        launcher = createRuntimeLauncher(owner)
        settingLauncher = createSystemLauncher(owner)
    }

    /**
     * To request notification runtime permission*/
    private fun createRuntimeLauncher(owner: LifecycleOwner): ActivityResultLauncher<PermissionName> {
        return registry.register("cameraLauncher", owner, ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // TODO: do nothing because everything we need is OK
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.CAMERA)
                ) {
                    // TODO: do nothing because Android system will request automatically
                } else {
                    callback.openRationaleDialog()
                }
            }
        }
    }

    /**
     * To open app setting*/
    private fun createSystemLauncher(owner: LifecycleOwner): ActivityResultLauncher<Intent> {
        return registry.register("SettingAppLauncher", owner, ActivityResultContracts.StartActivityForResult()){
            val enableAllPermissions = PermissionUtil.isCameraAccessible(context = activity)

            if (enableAllPermissions) {
                Toast.makeText(activity,
                    activity.getString(R.string.camera_enabled), Toast.LENGTH_SHORT).show()
            } else {
                callback.requestPermissionsOneMoreTime()
            }
        }
    }

    interface Callback {
        fun openRationaleDialog()

        fun requestPermissionsOneMoreTime()
    }
}