package pion.tech.pionbase.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.cacheRepository.CacheRepository
import pion.tech.pionbase.util.Result

class ClearCacheUseCase(
    private val cacheRepository: CacheRepository,
) {
    operator fun invoke(): Flow<Result<Unit>> = cacheRepository.clearCache()
}
