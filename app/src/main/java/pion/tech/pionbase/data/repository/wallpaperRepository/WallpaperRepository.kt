package pion.tech.pionbase.data.repository.wallpaperRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.util.Result

interface WallpaperRepository {
    fun getFeaturedWallpapers(): Flow<Result<List<WallpaperDtoModel>>>
    fun getTopWallpapers(): Flow<Result<List<WallpaperDtoModel>>>
    fun getCategories(): Flow<Result<List<CategoryDtoModel>>>
    fun getFavoriteWallpapers(): Flow<Result<List<WallpaperDtoModel>>>
    fun getWallpapersByCategory(categoryName: String): Flow<Result<List<WallpaperDtoModel>>>
    fun isFavorite(url: String): Flow<Result<Boolean>>
    fun searchWallpapers(query: String): Flow<Result<List<WallpaperDtoModel>>>
    suspend fun toggleFavorite(imageUrl: String): Result<Unit>
    suspend fun downloadWallpaper(url: String): Flow<Result<android.net.Uri>>
}
