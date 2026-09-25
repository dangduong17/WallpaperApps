package pion.tech.pionbase.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.history.WallpaperHistoryEntity

@Dao
interface WallpaperHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHistory(entity: WallpaperHistoryEntity)

    @Query("SELECT * FROM wallpaper_history WHERE imageUrl = :imageUrl AND type = :type LIMIT 1")
    suspend fun getHistoryByUrlAndType(imageUrl: String, type: String): WallpaperHistoryEntity?

    @Query("SELECT * FROM wallpaper_history WHERE type = 'VIEW' ORDER BY timestamp DESC")
    fun getRecentViews(): Flow<List<WallpaperHistoryEntity>>

    @Query("SELECT * FROM wallpaper_history WHERE type IN ('SET', 'DOWNLOAD') ORDER BY timestamp DESC")
    fun getDownloadAndSetHistory(): Flow<List<WallpaperHistoryEntity>>

    @Query("SELECT * FROM wallpaper_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<WallpaperHistoryEntity>>

    @Query("DELETE FROM wallpaper_history WHERE type = :type")
    suspend fun clearHistoryByType(type: String)

    @Query("DELETE FROM wallpaper_history WHERE type IN ('SET', 'DOWNLOAD')")
    suspend fun clearDownloadAndSetHistory()

    @Query("DELETE FROM wallpaper_history")
    suspend fun clearAllHistory()
}
