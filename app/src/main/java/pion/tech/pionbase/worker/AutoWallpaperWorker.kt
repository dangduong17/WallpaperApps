package pion.tech.pionbase.worker

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pion.tech.pionbase.data.repository.wallpaperRepository.WallpaperRepository
import pion.tech.pionbase.util.Result as AppResult
import timber.log.Timber

class AutoWallpaperWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val wallpaperRepository: WallpaperRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "AutoWallpaperWork"
        const val DEFAULT_INTERVAL = 60L
        const val MIN_INTERVAL = 15
        const val MAX_INTERVAL = 1440
        private var lastUsedUrl: String? = null
    }

    override suspend fun doWork(): androidx.work.ListenableWorker.Result = withContext(Dispatchers.IO) {
        Timber.d("AutoWallpaperWorker: doWork started")
        try {
            val favoriteResult = wallpaperRepository.getFavoriteWallpapers().first()
            Timber.d("AutoWallpaperWorker: Favorite result: $favoriteResult")

            if (favoriteResult is AppResult.Success) {
                val wallpapers = favoriteResult.data
                Timber.d("AutoWallpaperWorker: Total wallpapers in list: ${wallpapers.size}")
                if (wallpapers.isNotEmpty()) {
                    val availableWallpapers = if (wallpapers.size > 1) {
                        wallpapers.filter { it.imageUrl != lastUsedUrl }
                    } else {
                        wallpapers
                    }
                    val randomWallpaper = availableWallpapers.random()
                    lastUsedUrl = randomWallpaper.imageUrl
                    
                    val urlWithTimestamp = "${randomWallpaper.imageUrl}?t=${System.currentTimeMillis()}"
                    
                    Timber.d("AutoWallpaperWorker: Randomly selected: ${randomWallpaper.title}")
                    Timber.d("AutoWallpaperWorker: Downloading wallpaper: $urlWithTimestamp")
                    
                    val bitmap = downloadBitmap(urlWithTimestamp)
                    if (bitmap != null) {
                        Timber.d("AutoWallpaperWorker: Bitmap downloaded, setting wallpaper...")
                        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                        wallpaperManager.setBitmap(bitmap)
                        Timber.d("AutoWallpaperWorker: Wallpaper set successfully")
                        androidx.work.ListenableWorker.Result.success()
                    } else {
                        androidx.work.ListenableWorker.Result.retry()
                    }
                } else {
                    androidx.work.ListenableWorker.Result.failure()
                }
            } else {
                androidx.work.ListenableWorker.Result.retry()
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                Timber.d("AutoWallpaperWorker: Work was cancelled")
            } else {
                Timber.e(e, "AutoWallpaperWorker: Error during work")
            }
            androidx.work.ListenableWorker.Result.failure()
        }
    }

    private suspend fun downloadBitmap(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get()
            } catch (e: Exception) {
                Timber.e(e, "AutoWallpaperWorker: Error downloading bitmap")
                null
            }
        }
    }
}
