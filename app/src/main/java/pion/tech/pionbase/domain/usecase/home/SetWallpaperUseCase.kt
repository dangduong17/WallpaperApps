package pion.tech.pionbase.domain.usecase.home

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pion.tech.pionbase.util.Result

class SetWallpaperUseCase(
    private val context: Context,
) {
    operator fun invoke(bitmap: Bitmap): Flow<Result<Unit>> = flow {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(bitmap)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    operator fun invoke(uri: Uri, flag: Int): Flow<Result<Unit>> = flow {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                wallpaperManager.setStream(inputStream, null, true, flag)
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(Exception("InputStream is null")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
