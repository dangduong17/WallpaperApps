package pion.tech.pionbase.domain.usecase.home

import android.app.Application
import android.app.WallpaperManager
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pion.tech.pionbase.util.Result

class SetWallpaperUseCase(private val application: Application) {
    operator fun invoke(uri: Uri, which: Int = WallpaperManager.FLAG_SYSTEM): Flow<Result<Unit>> = flow {
        try {
            val wallpaperManager = WallpaperManager.getInstance(application)
            application.contentResolver.openInputStream(uri)?.use { stream ->
                wallpaperManager.setStream(stream, null, true, which)
            }
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
