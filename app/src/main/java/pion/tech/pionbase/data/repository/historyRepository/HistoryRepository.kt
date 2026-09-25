package pion.tech.pionbase.data.repository.historyRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.history.WallpaperHistoryDtoModel
import pion.tech.pionbase.util.Result

interface HistoryRepository {
    suspend fun addHistory(
        imageUrl: String,
        thumbnailUrl: String,
        title: String,
        categoryName: String,
        type: String
    ): Result<Unit>

    fun getRecentViews(): Flow<Result<List<WallpaperHistoryDtoModel>>>
    fun getDownloadAndSetHistory(): Flow<Result<List<WallpaperHistoryDtoModel>>>
    suspend fun clearHistory(type: String? = null): Result<Unit>
}
