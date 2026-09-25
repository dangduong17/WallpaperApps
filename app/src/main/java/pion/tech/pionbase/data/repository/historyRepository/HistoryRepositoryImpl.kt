package pion.tech.pionbase.data.repository.historyRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.database.dao.WallpaperHistoryDao
import pion.tech.pionbase.data.model.history.WallpaperHistoryDtoModel
import pion.tech.pionbase.data.model.history.WallpaperHistoryEntity
import pion.tech.pionbase.data.model.history.toDto
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result

class HistoryRepositoryImpl(
    private val wallpaperHistoryDao: WallpaperHistoryDao
) : BaseRepository(), HistoryRepository {

    override suspend fun addHistory(
        imageUrl: String,
        thumbnailUrl: String,
        title: String,
        categoryName: String,
        type: String
    ): Result<Unit> {
        return try {
            val existing = wallpaperHistoryDao.getHistoryByUrlAndType(imageUrl, type)
            val entity = WallpaperHistoryEntity(
                id = existing?.id ?: 0,
                imageUrl = imageUrl,
                thumbnailUrl = thumbnailUrl,
                title = title,
                categoryName = categoryName,
                timestamp = System.currentTimeMillis(),
                type = type
            )
            wallpaperHistoryDao.insertOrUpdateHistory(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(e)
        }
    }

    override fun getRecentViews(): Flow<Result<List<WallpaperHistoryDtoModel>>> {
        return wallpaperHistoryDao.getRecentViews().executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
        }
    }

    override fun getDownloadAndSetHistory(): Flow<Result<List<WallpaperHistoryDtoModel>>> {
        return wallpaperHistoryDao.getDownloadAndSetHistory().executeDataWithFlowCall { entities ->
            entities.map { it.toDto() }
        }
    }

    override suspend fun clearHistory(type: String?): Result<Unit> {
        return try {
            when (type) {
                "VIEW" -> wallpaperHistoryDao.clearHistoryByType("VIEW")
                "SET_DOWNLOAD" -> wallpaperHistoryDao.clearDownloadAndSetHistory()
                else -> {
                    if (!type.isNullOrEmpty()) {
                        wallpaperHistoryDao.clearHistoryByType(type)
                    } else {
                        wallpaperHistoryDao.clearAllHistory()
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(e)
        }
    }
}
