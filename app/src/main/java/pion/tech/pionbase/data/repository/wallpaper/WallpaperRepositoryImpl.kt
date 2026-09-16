package pion.tech.pionbase.data.repository.wallpaper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.remote.wallpaper.WallpaperDataSource
import pion.tech.pionbase.util.Result

class WallpaperRepositoryImpl(
    private val dataSource: WallpaperDataSource
) : WallpaperRepository {
    override fun getFeaturedWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow<Result<List<WallpaperDtoModel>>> {
        emit(Result.Success(dataSource.getFeaturedWallpapers()))
    }.flowOn(Dispatchers.IO).catch { emit(Result.Error(it)) }

    override fun getTopWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = flow<Result<List<WallpaperDtoModel>>> {
        emit(Result.Success(dataSource.getTopWallpapers()))
    }.flowOn(Dispatchers.IO).catch { emit(Result.Error(it)) }

    override fun getCategories(): Flow<Result<List<CategoryDtoModel>>> = flow<Result<List<CategoryDtoModel>>> {
        emit(Result.Success(dataSource.getCategories()))
    }.flowOn(Dispatchers.IO).catch { emit(Result.Error(it)) }
}
