package pion.tech.pionbase.data.repository.wallpaper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.data.database.dao.CategoryDao
import pion.tech.pionbase.data.database.dao.WallpaperDao
import pion.tech.pionbase.data.model.wallpaper.*
import pion.tech.pionbase.data.remote.wallpaper.WallpaperDataSource
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result

@OptIn(ExperimentalCoroutinesApi::class)
class WallpaperRepositoryImpl(
    private val dataSource: WallpaperDataSource,
    private val wallpaperDao: WallpaperDao,
    private val categoryDao: CategoryDao
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
}
