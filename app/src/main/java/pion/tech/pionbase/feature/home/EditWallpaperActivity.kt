package pion.tech.pionbase.feature.home

import android.app.AlertDialog
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
import pion.tech.pionbase.service.LiveWallpaperService
import pion.tech.pionbase.util.isGif
import pion.tech.pionbase.util.parcelable
import java.io.File
import java.io.FileInputStream
import android.app.WallpaperManager



class EditWallpaperActivity : AppCompatActivity() {

    private val cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = result.data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                try {
                    val finalFile = File(filesDir, "active_final_wallpaper.jpg")
                    contentResolver.openInputStream(resultUri)?.use { input ->
                        finalFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    applyWallpaper(Uri.fromFile(finalFile))
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.error_reading_file, e.message), Toast.LENGTH_LONG).show()
                }
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Toast.makeText(this, getString(R.string.error_crop, cropError?.message), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val uri = intent.parcelable<Uri>("uri") ?: return finish()

        val isCropped = intent.getBooleanExtra("is_cropped", false)
        
        if (uri.isGif(contentResolver) || isCropped) {
            applyWallpaper(uri)
        } else {
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_wallpaper_" + System.currentTimeMillis() + ".jpg"))
            cropLauncher.launch(UCrop.of(uri, destinationUri).withAspectRatio(9f, 16f).getIntent(this))
        }
    }

    private fun applyWallpaper(uri: Uri) {
        val isGif = uri.isGif(contentResolver)

        if (isGif) {
            try {
                val outputFile = File(filesDir, "active_gif.gif")
                contentResolver.openInputStream(uri)?.use { input ->
                    outputFile.outputStream().use { output -> 
                        input.copyTo(output)
                        output.flush()
                    }
                }
                outputFile.setReadable(true, false)
                
                getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
                    .edit().putString("selected_gif_path", outputFile.absolutePath).apply()

                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(this, LiveWallpaperService::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                mainIntent.putExtra("show_success_msg", true)
                startActivity(mainIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.error_set_gif, e.message), Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            // Hiển thị dialog chọn màn hình
            val options = arrayOf(getString(R.string.home_screen), getString(R.string.lock_screen), getString(R.string.both))
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.set_wallpaper))
                .setItems(options) { _, which ->
                    try {
                        val finalFile = File(filesDir, "active_static_wallpaper.jpg")
                        contentResolver.openInputStream(uri)?.use { input ->
                            finalFile.outputStream().use { output -> input.copyTo(output) }
                        }
                        
                        val wallpaperManager = WallpaperManager.getInstance(this)
                        val inputStream = FileInputStream(finalFile)
                        
                        val flag = when (which) {
                            0 -> WallpaperManager.FLAG_SYSTEM
                            1 -> WallpaperManager.FLAG_LOCK
                            else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                        }
                        
                        wallpaperManager.setStream(inputStream, null, true, flag)
                        
                        val mainIntent = Intent(this, MainActivity::class.java)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        mainIntent.putExtra("show_success_msg", true)
                        startActivity(mainIntent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this, getString(R.string.error, e.message), Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                .show()
        }
    }
}
