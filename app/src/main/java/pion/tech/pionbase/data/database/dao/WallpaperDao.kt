package pion.tech.pionbase.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.wallpaper.WallpaperEntity

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpapers")
    fun getAllWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE isFeatured = 1")
    fun getFeaturedWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE isFavorite = 1")
    fun getFavoriteWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE categoryName = :categoryName")
    fun getWallpapersByCategory(categoryName: String): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE title LIKE '%' || :query || '%'")
    fun searchWallpapers(query: String): Flow<List<WallpaperEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpapers(wallpapers: List<WallpaperEntity>)

    @Update
    suspend fun updateWallpaper(wallpaper: WallpaperEntity)

    @Query("SELECT * FROM wallpapers WHERE imageUrl = :url LIMIT 1")
    suspend fun getWallpaperByUrl(url: String): WallpaperEntity?

    @Query("SELECT isFavorite FROM wallpapers WHERE imageUrl = :url LIMIT 1")
    fun isFavorite(url: String): Flow<Boolean>
}
