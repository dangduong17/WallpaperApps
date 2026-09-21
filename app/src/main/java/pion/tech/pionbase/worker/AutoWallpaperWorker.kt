package pion.tech.pionbase.worker

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.util.Result
import kotlin.random.Random

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
    }

    override suspend fun doWork(): androidx.work.ListenableWorker.Result = withContext(Dispatchers.IO) {
        try {
            val favoriteResult = wallpaperRepository.getFavoriteWallpapers().first()
            if (favoriteResult is pion.tech.pionbase.util.Result.Success) {
                val wallpapers = favoriteResult.data
                if (wallpapers.isNotEmpty()) {
                    val randomWallpaper = wallpapers[Random.nextInt(wallpapers.size)]
                    val bitmap = downloadBitmap(randomWallpaper.imageUrl)
                    if (bitmap != null) {
                        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                        wallpaperManager.setBitmap(bitmap)
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
                null
            }
        }
    }
}
