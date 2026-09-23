package pion.tech.pionbase.data.repository.cacheRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result

interface CacheRepository {
    fun getCacheSize(): Flow<Result<Long>>
    fun clearCache(): Flow<Result<Unit>>
}
