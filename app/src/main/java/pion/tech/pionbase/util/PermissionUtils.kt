package pion.tech.pionbase.util

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.permissionx.guolindev.PermissionX
import pion.tech.pionbase.R
import pion.tech.pionbase.base.doActionWhenStop

fun Fragment.requestPermissionInSetting(
    launcher: ActivityResultLauncher<Intent>,
    intent: Intent,
) {
    doActionWhenStop {
        //TODO : AdsController
//        AdsController.isBlockOpenAds = true
    }
    runCatching {
        launcher.launch(intent)
    }.onFailure {
        displayToast(R.string.something_error)
    }
}

/**
 * Convenience extension function to request permissions using PermissionX in Fragment
 */
fun Fragment.requestPermissionsWithPermissionX(
    permissions: List<String>,
    explainMessage: String? = null,
    onGranted: () -> Unit,
    onDenied: ((deniedList: List<String>) -> Unit)? = null
) {
    if (permissions.isEmpty()) {
        onGranted()
        return
    }

    val request = PermissionX.init(this)
        .permissions(permissions)

    explainMessage?.let { msg ->
        request.onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(deniedList, msg, getString(R.string.ok), getString(R.string.cancel))
        }
    }

    request.request { allGranted, _, deniedList ->
        if (allGranted) {
            onGranted()
        } else {
            if (onDenied != null) {
                onDenied(deniedList)
            } else {
                displayToast(R.string.need_permission_to_save)
            }
        }
    }
}
