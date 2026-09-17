package pion.tech.pionbase.data.repository.wallpaper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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

    override fun getFeaturedWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow {
        val dbWallpapers = wallpaperDao.getFeaturedWallpapers().first()
        if (dbWallpapers.isEmpty()) {
            val remoteFeatured = dataSource.getFeaturedWallpapers()
            wallpaperDao.insertWallpapers(remoteFeatured.map { 
                WallpaperEntity(title = it.title, imageUrl = it.imageUrl, categoryName = it.categoryName, isFeatured = true) 
            })
        }
        emit(Unit)
    }.flatMapLatest {
        wallpaperDao.getFeaturedWallpapers().map { entities -> 
            Result.Success(entities.map { it.toDto() }) 
        }
    }

    override fun getTopWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow {
        val dbWallpapers = wallpaperDao.getAllWallpapers().first()
        if (dbWallpapers.isEmpty()) {
            val remoteTop = dataSource.getTopWallpapers()
            wallpaperDao.insertWallpapers(remoteTop.map { 
                WallpaperEntity(title = it.title, imageUrl = it.imageUrl, categoryName = it.categoryName, isFeatured = false) 
            })
        }
        emit(Unit)
    }.flatMapLatest {
        wallpaperDao.getAllWallpapers().map { entities -> 
            Result.Success(entities.map { it.toDto() }) 
        }
    }

    override fun getCategories(): Flow<Result<List<CategoryDtoModel>>> = flow {
        val dbCategories = categoryDao.getAllCategories().first()
        if (dbCategories.isEmpty()) {
            val remoteCategories = dataSource.getCategories()
            categoryDao.insertCategories(remoteCategories.map { 
                CategoryEntity(title = it.title, imageUrl = it.imageUrl) 
            })
        }
        emit(Unit)
    }.flatMapLatest {
        categoryDao.getAllCategories().map { entities -> 
            Result.Success(entities.map { it.toDto() }) 
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
