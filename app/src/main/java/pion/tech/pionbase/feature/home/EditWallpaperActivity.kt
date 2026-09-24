package pion.tech.pionbase.feature.home

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.ucrop.UCrop
import pion.tech.pionbase.R
import pion.tech.pionbase.app.MainActivity
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.service.LiveWallpaperService
import pion.tech.pionbase.service.VideoWallpaperService
import pion.tech.pionbase.util.Constant
import pion.tech.pionbase.util.isGif
import pion.tech.pionbase.util.isVideo
import pion.tech.pionbase.util.parcelable
import java.io.File
import java.io.FileInputStream

class EditWallpaperActivity : AppCompatActivity() {

    private val liveWallpaperLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.KEY_SHOW_SUCCESS_MSG, true)
        }
        startActivity(mainIntent)
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private val cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = result.data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                launchIO(
                    onError = { e ->
                        launchMain {
                            showToast(getString(R.string.error_reading_file, e.message))
                            finish()
                        }
                    }
                ) {
                    val finalFile = File(filesDir, Constant.FILE_ACTIVE_STATIC)
                    contentResolver.openInputStream(resultUri)?.use { input ->
                        finalFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    launchMain {
                        applyWallpaper(Uri.fromFile(finalFile))
                    }
                }
            } else {
                finish()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            showToast(getString(R.string.error_crop, cropError?.message))
            finish()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val uri = intent.parcelable<Uri>(Constant.KEY_URI) ?: return finish()

        val isCropped = intent.getBooleanExtra(Constant.KEY_IS_CROPPED, false)
        
        if (uri.isGif(contentResolver) || uri.isVideo(contentResolver) || isCropped) {
            applyWallpaper(uri)
        } else {
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_wallpaper_" + System.currentTimeMillis() + ".jpg"))
            cropLauncher.launch(UCrop.of(uri, destinationUri).withAspectRatio(9f, 16f).getIntent(this))
        }
    }

    private fun applyWallpaper(uri: Uri) {
        val isGif = uri.isGif(contentResolver)
        val isVideo = uri.isVideo(contentResolver)

        if (isVideo) {
            setupLiveWallpaper(
                uri = uri,
                fileName = Constant.FILE_ACTIVE_VIDEO,
                pathKey = Constant.KEY_WALLPAPER_PATH,
                timeKey = Constant.KEY_VIDEO_UPDATED_AT,
                serviceClass = VideoWallpaperService::class.java,
                errorResId = R.string.error_set_video
            )
        } else if (isGif) {
            setupLiveWallpaper(
                uri = uri,
                fileName = Constant.FILE_ACTIVE_GIF,
                pathKey = Constant.KEY_SELECTED_GIF_PATH,
                timeKey = Constant.KEY_GIF_UPDATED_AT,
                serviceClass = LiveWallpaperService::class.java,
                errorResId = R.string.error_set_gif
            )
        } else {
            showStaticWallpaperDialog(uri)
        }
    }

    private fun setupLiveWallpaper(
        uri: Uri,
        fileName: String,
        pathKey: String,
        timeKey: String,
        serviceClass: Class<*>,
        errorResId: Int
    ) {
        launchIO(
            onError = { e ->
                launchMain {
                    showToast(getString(errorResId, e.message ?: ""))
                    finish()
                }
            }
        ) {
            val outputFile = File(filesDir, fileName)
            contentResolver.openInputStream(uri)?.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }
            outputFile.setReadable(true, false)

            getSharedPreferences(Constant.PREF_WALLPAPER, MODE_PRIVATE)
                .edit()
                .putString(pathKey, outputFile.absolutePath)
                .putLong(timeKey, System.currentTimeMillis())
                .apply()

            launchMain {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                    putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(this@EditWallpaperActivity, serviceClass)
                    )
                }
                try {
                    liveWallpaperLauncher.launch(intent)
                } catch (_: Exception) {
                    showToast(getString(R.string.error_live_wallpaper_not_supported))
                    finish()
                }
            }
        }
    }

    private fun showStaticWallpaperDialog(uri: Uri) {
        val options = arrayOf(getString(R.string.home_screen), getString(R.string.lock_screen), getString(R.string.both))
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.set_wallpaper))
            .setItems(options) { _, which ->
                launchIO(
                    onError = { e ->
                        launchMain {
                            showToast(getString(R.string.error, e.message ?: ""))
                            finish()
                        }
                    }
                ) {
                    val finalFile = File(filesDir, Constant.FILE_ACTIVE_STATIC)
                    contentResolver.openInputStream(uri)?.use { input ->
                        finalFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    
                    val wallpaperManager = WallpaperManager.getInstance(this@EditWallpaperActivity)
                    val inputStream = FileInputStream(finalFile)
                    
                    val flag = when (which) {
                        0 -> WallpaperManager.FLAG_SYSTEM
                        1 -> WallpaperManager.FLAG_LOCK
                        else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                    }
                    
                    wallpaperManager.setStream(inputStream, null, true, flag)
                    
                    launchMain {
                        val mainIntent = Intent(this@EditWallpaperActivity, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            putExtra(Constant.KEY_SHOW_SUCCESS_MSG, true)
                        }
                        startActivity(mainIntent)
                        finish()
                    }
                }
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }
}