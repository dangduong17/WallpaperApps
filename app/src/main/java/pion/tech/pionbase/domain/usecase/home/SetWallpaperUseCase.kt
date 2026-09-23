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
    operator fun invoke(bitmap: Bitmap, flag: Int = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK): Flow<Result<Unit>> = flow {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val cropHint = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
            wallpaperManager.setBitmap(bitmap, cropHint, false, flag)
            emit(Result.Success(Unit))
        } catch (e: Throwable) {
            if (e is android.os.DeadObjectException || e.cause is android.os.DeadObjectException || e.toString().contains("DeadObjectException")) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(if (e is Exception) e else Exception(e)))
            }
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
        } catch (e: Throwable) {
            if (e is android.os.DeadObjectException || e.cause is android.os.DeadObjectException || e.toString().contains("DeadObjectException")) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(if (e is Exception) e else Exception(e)))
            }
        }
    }
}
