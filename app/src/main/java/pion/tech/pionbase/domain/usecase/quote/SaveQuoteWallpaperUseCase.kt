package pion.tech.pionbase.domain.usecase.quote

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.util.Result
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class SaveQuoteWallpaperUseCase(
    private val context: Context
) {
    operator fun invoke(bitmap: Bitmap, filenamePrefix: String = "quote_wallpaper_"): Flow<Result<Uri>> = flow<Result<Uri>> {
        try {
            val filename = "${filenamePrefix}${System.currentTimeMillis()}.jpg"
            val uri: Uri?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wallpapers")
                }
                val resolver = context.contentResolver
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val imageUri = resolver.insert(contentUri, contentValues)
                    ?: throw Exception("Failed to create MediaStore entry")
                
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        throw Exception("Failed to save bitmap")
                    }
                }
                uri = imageUri
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val wallpaperFolder = File(imagesDir, "Wallpapers")
                if (!wallpaperFolder.exists()) {
                    wallpaperFolder.mkdirs()
                }
                val imageFile = File(wallpaperFolder, filename)
                val outputStream: OutputStream = FileOutputStream(imageFile)
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    outputStream.close()
                    throw Exception("Failed to save bitmap")
                }
                outputStream.flush()
                outputStream.close()

                MediaScannerConnection.scanFile(context, arrayOf(imageFile.toString()), null, null)
                uri = Uri.fromFile(imageFile)
            }

            if (uri != null) {
                emit(Result.Success(uri))
            } else {
                emit(Result.Error(Exception("Uri is null")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
