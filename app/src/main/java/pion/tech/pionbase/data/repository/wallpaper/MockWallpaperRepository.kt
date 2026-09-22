package pion.tech.pionbase.data.repository.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pion.tech.pionbase.data.database.dao.WallpaperDao
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.model.wallpaper.toEntity
import pion.tech.pionbase.util.Result
import timber.log.Timber

class MockWallpaperRepository(
    private val context: Context,
    private val gson: Gson,
    private val wallpaperDao: WallpaperDao
) : WallpaperRepository {

    override fun getFavoriteWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = 
        wallpaperDao.getFavoriteWallpapers().map { entities ->
            val dtos = entities.map { WallpaperDtoModel(it.title, it.imageUrl, it.categoryName) }
            Result.Success(dtos)
        }

    override fun getFeaturedWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow { emit(loadFromAssets()) }

    override fun getTopWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow { emit(loadFromAssets()) }

    override fun getCategories(): Flow<Result<List<CategoryDtoModel>>> = flow {
        val all = loadFromAssets()
        if (all is Result.Success) {
            val categories = all.data
                .map { it.categoryName }
                .distinct()
                .map { category ->
                    val imageUrl = all.data.firstOrNull { it.categoryName == category }?.imageUrl ?: ""
                    CategoryDtoModel(title = category, imageUrl = imageUrl)
                }
            emit(Result.Success(categories))
        } else {
            val error = (all as Result.Error).error
            emit(Result.Error(error))
        }
    }

    override fun getWallpapersByCategory(categoryName: String): Flow<Result<List<WallpaperDtoModel>>> = flow {
        val all = loadFromAssets()
        if (all is Result.Success) {
            emit(Result.Success(all.data.filter { it.categoryName.equals(categoryName, true) }))
        } else {
            emit(all as Result.Error)
        }
    }

    override fun isFavorite(url: String): Flow<Result<Boolean>> = 
        wallpaperDao.isFavorite(url).map { Result.Success(it) }

    override fun searchWallpapers(query: String): Flow<Result<List<WallpaperDtoModel>>> = flow {
        val all = loadFromAssets()
        if (all is Result.Success) {
            emit(Result.Success(all.data.filter { it.title.contains(query, true) }))
        } else {
            emit(all as Result.Error)
        }
    }

    override suspend fun toggleFavorite(imageUrl: String): Result<Unit> {
        val entity = wallpaperDao.getWallpaperByUrl(imageUrl)
        return if (entity != null) {
            val updated = entity.copy(isFavorite = !entity.isFavorite)
            wallpaperDao.updateWallpaper(updated)
            Result.Success(Unit)
        } else {
            val all = loadFromAssets()
            if (all is Result.Success) {
                val found = all.data.find { it.imageUrl == imageUrl }
                if (found != null) {
                    val newEntity = found.toEntity().copy(isFavorite = true)
                    wallpaperDao.insertWallpapers(listOf(newEntity))
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception("Wallpaper not found"))
                }
            } else {
                Result.Error(Exception("Failed to load assets"))
            }
        }
    }

    override suspend fun downloadWallpaper(url: String): Flow<Result<Uri>> = flow {
        Timber.d("downloadWallpaper: Starting download for $url")
        try {
            val uri = withContext(Dispatchers.IO) {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
                
                // Lưu vào thư mục công cộng Pictures thay vì thư mục riêng của app
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!directory.exists()) directory.mkdirs()
                val file = java.io.File(directory, "wallpaper_${System.currentTimeMillis()}.jpg")
                
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                
                // Scan file để Gallery nhận diện
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
                
                Uri.fromFile(file)
            }
            Timber.d("downloadWallpaper: Success, URI: $uri")
            emit(Result.Success(uri))
        } catch (e: Exception) {
            Timber.e(e, "downloadWallpaper: Failed for $url")
            emit(Result.Error(e))
        }
    }

    private fun loadFromAssets(): Result<List<WallpaperDtoModel>> {
        return try {
            val json = context.assets.open("wallpapers.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<WallpaperDtoModel>>() {}.type
            val data: List<WallpaperDtoModel> = gson.fromJson(json, listType)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
