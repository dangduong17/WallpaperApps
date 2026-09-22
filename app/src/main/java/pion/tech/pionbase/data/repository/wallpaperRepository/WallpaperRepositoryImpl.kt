package pion.tech.pionbase.data.repository.wallpaperRepository

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.Request
import pion.tech.pionbase.data.database.dao.CategoryDao
import pion.tech.pionbase.data.database.dao.WallpaperDao
import pion.tech.pionbase.data.model.wallpaper.*
import pion.tech.pionbase.data.remote.wallpaper.WallpaperDataSource
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result
import java.io.OutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class WallpaperRepositoryImpl(
    private val dataSource: WallpaperDataSource,
    private val wallpaperDao: WallpaperDao,
    private val categoryDao: CategoryDao,
    private val okHttpClient: OkHttpClient,
    private val context: android.content.Context
) : BaseRepository(), WallpaperRepository {

    override fun getFeaturedWallpapers(): Flow<Result<List<WallpaperDtoModel>>> =
        wallpaperDao.getFeaturedWallpapers().flatMapLatest { entities ->
            if (entities.isEmpty()) {
                flow<Result<List<WallpaperDtoModel>>> {
                    val remote = dataSource.getFeaturedWallpapers()
                    if (remote.isNotEmpty()) {
                        val localWallpapers = wallpaperDao.getAllWallpapersList().associateBy { it.imageUrl }
                        val merged = remote.map { dto ->
                            val local = localWallpapers[dto.imageUrl]
                            dto.toEntity(isFeatured = true).copy(isFavorite = local?.isFavorite ?: false)
                        }
                        wallpaperDao.insertWallpapers(merged)
                    } else {
                        emit(Result.Success(emptyList()))
                    }
                }.catch { emit(Result.Error(it)) }
            } else {
                flowOf(Result.Success(entities.map { it.toDto() }))
            }
        }

    override fun getTopWallpapers(): Flow<Result<List<WallpaperDtoModel>>> =
        wallpaperDao.getAllWallpapers().flatMapLatest { entities ->
            if (entities.isEmpty()) {
                flow<Result<List<WallpaperDtoModel>>> {
                    val remote = dataSource.getTopWallpapers()
                    if (remote.isNotEmpty()) {
                        val localWallpapers = wallpaperDao.getAllWallpapersList().associateBy { it.imageUrl }
                        val merged = remote.map { dto ->
                            val local = localWallpapers[dto.imageUrl]
                            dto.toEntity(isFeatured = false).copy(isFavorite = local?.isFavorite ?: false)
                        }
                        wallpaperDao.insertWallpapers(merged)
                    } else {
                        emit(Result.Success(emptyList()))
                    }
                }.catch { emit(Result.Error(it)) }
            } else {
                flowOf(Result.Success(entities.map { it.toDto() }))
            }
        }

    override fun getCategories(): Flow<Result<List<CategoryDtoModel>>> =
        categoryDao.getAllCategories().flatMapLatest { entities ->
            if (entities.isEmpty()) {
                flow<Result<List<CategoryDtoModel>>> {
                    val remote = dataSource.getCategories()
                    if (remote.isNotEmpty()) {
                        categoryDao.insertCategories(remote.map { it.toEntity() })
                    } else {
                        emit(Result.Success(emptyList()))
                    }
                }.catch { emit(Result.Error(it)) }
            } else {
                flowOf(Result.Success(entities.map { it.toDto() }))
            }
        }

    override fun getFavoriteWallpapers(): Flow<Result<List<WallpaperDtoModel>>> {
        return wallpaperDao.getFavoriteWallpapers().map { entities ->
            Result.Success(entities.map { it.toDto() })
        }
    }

    override fun getWallpapersByCategory(categoryName: String): Flow<Result<List<WallpaperDtoModel>>> {
        return wallpaperDao.getWallpapersByCategory(categoryName).map { entities ->
            Result.Success(entities.map { it.toDto() })
        }
    }

    override fun isFavorite(url: String): Flow<Result<Boolean>> {
        return wallpaperDao.isFavorite(url).map { Result.Success(it) }
    }

    override fun searchWallpapers(query: String): Flow<Result<List<WallpaperDtoModel>>> {
        return wallpaperDao.searchWallpapers(query).map { entities ->
            Result.Success(entities.map { it.toDto() })
        }
    }

    override suspend fun toggleFavorite(imageUrl: String): Result<Unit> {
        return try {
            val wallpaper = wallpaperDao.getWallpaperByUrl(imageUrl)
            if (wallpaper != null) {
                wallpaperDao.updateWallpaper(wallpaper.copy(isFavorite = !wallpaper.isFavorite))
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Wallpaper not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun downloadWallpaper(url: String): Flow<Result<Uri>> = flow {
        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Failed to download")
        
        val bitmap = response.body?.byteStream()?.use { BitmapFactory.decodeStream(it) } 
            ?: throw Exception("Failed to decode image")

        val filename = "PionBase_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PionBase")
            }
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw Exception("Failed to create media store entry")

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
        } ?: throw Exception("Failed to open output stream")

        val result: Result<Uri> = Result.Success(uri)
        emit(result)
    }.catch { 
        timber.log.Timber.e(it, "Download error")
        emit(Result.Error(it)) 
    }.flowOn(Dispatchers.IO)
}
