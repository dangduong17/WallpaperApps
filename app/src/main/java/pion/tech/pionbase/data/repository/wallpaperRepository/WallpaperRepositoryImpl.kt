package pion.tech.pionbase.data.repository.wallpaperRepository

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import pion.tech.pionbase.data.database.dao.CategoryDao
import pion.tech.pionbase.data.database.dao.WallpaperDao
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperEntity
import pion.tech.pionbase.data.model.wallpaper.toDto
import pion.tech.pionbase.data.model.wallpaper.toEntity
import pion.tech.pionbase.data.remote.wallpaper.WallpaperDataSource
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result
import timber.log.Timber

private const val FEATURED_LIMIT = 10

@OptIn(ExperimentalCoroutinesApi::class)
class WallpaperRepositoryImpl(
    private val dataSource: WallpaperDataSource,
    private val wallpaperDao: WallpaperDao,
    private val categoryDao: CategoryDao,
    private val okHttpClient: OkHttpClient,
    private val context: android.content.Context
) : BaseRepository(), WallpaperRepository {

    override fun getFeaturedWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow {
        try {
            val remote = dataSource.getFeaturedWallpapers()
            mergeAndSaveWallpapers(
                remote = remote,
                isFeaturedProvider = { index, local -> index < FEATURED_LIMIT || local?.isFeatured == true }
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e(e, "Error fetching remote featured wallpapers")
        }
        emit(Unit)
    }.flatMapLatest {
        wallpaperDao.getFeaturedWallpapers().executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
        }
    }

    override fun getTopWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow {
        try {
            val remote = dataSource.getTopWallpapers()
            mergeAndSaveWallpapers(
                remote = remote,
                deleteOldNonFavorites = true
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e(e, "Error fetching remote top wallpapers")
        }
        emit(Unit)
    }.flatMapLatest {
        wallpaperDao.getAllWallpapers().executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
        }
    }

    override fun getCategories(): Flow<Result<List<CategoryDtoModel>>> = flow {
        try {
            val remote = dataSource.getCategories()
            if (remote.isNotEmpty()) {
                categoryDao.deleteAllCategories()
                categoryDao.insertCategories(remote.map { it.toEntity() })
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e(e, "Error fetching remote categories")
        }
        emit(Unit)
    }.flatMapLatest {
        categoryDao.getAllCategories().executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
        }
    }

    override fun getFavoriteWallpapers(): Flow<Result<List<WallpaperDtoModel>>> {
        return wallpaperDao.getFavoriteWallpapers().executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
        }
    }

    override fun getWallpapersByCategory(categoryName: String): Flow<Result<List<WallpaperDtoModel>>> = flow {
        try {
            val remote = dataSource.getWallpapersByCategory(categoryName)
            mergeAndSaveWallpapers(remote = remote)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e(e, "Error fetching remote wallpapers by category: $categoryName")
        }
        emit(Unit)
    }.flatMapLatest {
        wallpaperDao.getWallpapersByCategory(categoryName).executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
        }
    }

    override fun isFavorite(url: String): Flow<Result<Boolean>> {
        return wallpaperDao.isFavorite(url).executeDataWithFlowCall { it }
    }

    override fun searchWallpapers(query: String): Flow<Result<List<WallpaperDtoModel>>> {
        return wallpaperDao.searchWallpapers(query).executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
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
            if (e is CancellationException) throw e
            Result.Error(e)
        }
    }

    override suspend fun downloadWallpaper(url: String): Flow<Result<Uri>> = flow<Result<Uri>> {
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        } ?: throw Exception("Failed to open output stream")

        emit(Result.Success(uri))
    }.catch {
        if (it is CancellationException) throw it
        Timber.e(it, "Download error")
        emit(Result.Error(it))
    }.flowOn(Dispatchers.IO)

    private suspend fun mergeAndSaveWallpapers(
        remote: List<WallpaperDtoModel>,
        isFeaturedProvider: (Int, WallpaperEntity?) -> Boolean = { _, local -> local?.isFeatured ?: false },
        deleteOldNonFavorites: Boolean = false
    ) {
        if (remote.isEmpty()) return
        val localWallpapers = wallpaperDao.getAllWallpapersList().associateBy { it.imageUrl }
        val merged = remote.mapIndexed { index, dto ->
            val local = localWallpapers[dto.imageUrl]
            dto.toEntity(isFeatured = isFeaturedProvider(index, local)).copy(
                id = local?.id ?: 0,
                isFavorite = local?.isFavorite ?: false
            )
        }
        if (deleteOldNonFavorites) {
            wallpaperDao.deleteNonFavoriteWallpapers()
        }
        wallpaperDao.insertWallpapers(merged)
    }
}
