package pion.tech.pionbase.feature.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.ucrop.UCrop
import java.io.File
import pion.tech.pionbase.service.LiveWallpaperService
import android.app.WallpaperManager
import android.content.ComponentName

class EditWallpaperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("uri", Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Uri>("uri")
        } ?: return finish()
        
        val mimeType = contentResolver.getType(uri)
        val isGif = mimeType == "image/gif" || uri.toString().endsWith(".gif")
        
        if (isGif) {
            applyWallpaper(uri)
        } else {
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_wallpaper_" + System.currentTimeMillis() + ".jpg"))
            UCrop.of(uri, destinationUri)
                .withAspectRatio(9f, 16f)
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                // COPY FILE THAY VÌ TRUYỀN URI:
                // Việc này loại bỏ hoàn toàn việc phải cấp quyền cho URI file://
                try {
                    val finalFile = File(filesDir, "active_final_wallpaper.jpg")
                    contentResolver.openInputStream(resultUri)?.use { input ->
                        finalFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    
                    // Sau khi copy xong, gọi hàm apply với URI của file nội bộ này
                    applyWallpaper(Uri.fromFile(finalFile))
                } catch (e: Exception) {
                    android.widget.Toast.makeText(this, "Lỗi đọc file: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            android.widget.Toast.makeText(this, "Lỗi Crop: ${cropError?.message}", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun applyWallpaper(uri: Uri) {
        val mimeType = contentResolver.getType(uri)
        val isGif = mimeType == "image/gif" || uri.toString().endsWith(".gif")

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
                
                val mainIntent = Intent(this, pion.tech.pionbase.app.MainActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                mainIntent.putExtra("show_success_msg", true)
                startActivity(mainIntent)
                finish()
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Lỗi không thể set GIF: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            // LUỒNG ẢNH TĨNH: Copy nguyên vẹn, không nén để giữ chất lượng
            try {
                val finalFile = File(filesDir, "active_static_wallpaper.jpg")
                contentResolver.openInputStream(uri)?.use { input ->
                    finalFile.outputStream().use { output -> input.copyTo(output) }
                }
                
                val wallpaperManager = WallpaperManager.getInstance(this)
                wallpaperManager.setStream(contentResolver.openInputStream(Uri.fromFile(finalFile)))
                
                val mainIntent = Intent(this, pion.tech.pionbase.app.MainActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                mainIntent.putExtra("show_success_msg", true)
                startActivity(mainIntent)
                finish()
            } catch (e: Exception) {
                finish()
            }
        }
    }
}
