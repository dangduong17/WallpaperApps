package pion.tech.pionbase.data.repository.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.remote.wallpaper.WallpaperDataSource
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result

class WallpaperRepositoryImpl(
    private val dataSource: WallpaperDataSource
) : BaseRepository(), WallpaperRepository {
    override fun getFeaturedWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = executeDataCall {
        dataSource.getFeaturedWallpapers()
    }

    override fun getTopWallpapers(): Flow<Result<List<WallpaperDtoModel>>> = executeDataCall {
        dataSource.getTopWallpapers()
    }

    override fun getCategories(): Flow<Result<List<CategoryDtoModel>>> = executeDataCall {
        dataSource.getCategories()
    }
}
