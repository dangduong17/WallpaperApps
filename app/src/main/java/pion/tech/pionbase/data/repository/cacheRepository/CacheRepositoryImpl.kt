package pion.tech.pionbase.data.repository.cacheRepository

import android.content.Context
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result
import java.io.File

class CacheRepositoryImpl(
    private val context: Context,
) : BaseRepository(), CacheRepository {

    override fun getCacheSize(): Flow<Result<Long>> = executeDataCall {
        calculateDirSize(context.cacheDir) + (context.externalCacheDir?.let { calculateDirSize(it) } ?: 0L)
    }

    override fun clearCache(): Flow<Result<Unit>> = executeDataCall {
        // Clear Glide disk cache (runs on IO thread via executeDataCall)
        Glide.get(context).clearDiskCache()

        // Clear Glide memory cache (must run on Main thread)
        withContext(Dispatchers.Main) {
            Glide.get(context).clearMemory()
        }

        // Delete cache directory contents
        deleteDirContents(context.cacheDir)
        context.externalCacheDir?.let { deleteDirContents(it) }
    }

    private fun calculateDirSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var totalSize = 0L
        val files = dir.listFiles() ?: return 0L
        for (file in files) {
            totalSize += if (file.isDirectory) {
                calculateDirSize(file)
            } else {
                file.length()
            }
        }
        return totalSize
    }

    private fun deleteDirContents(dir: File?): Boolean {
        if (dir == null || !dir.exists()) return false
        val files = dir.listFiles() ?: return false
        var success = true
        for (file in files) {
            if (file.isDirectory) {
                deleteDirContents(file)
            }
            if (!file.delete()) {
                success = false
            }
        }
        return success
    }
}
