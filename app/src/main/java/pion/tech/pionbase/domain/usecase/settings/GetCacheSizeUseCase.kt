package pion.tech.pionbase.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.cacheRepository.CacheRepository
import pion.tech.pionbase.util.Result

class GetCacheSizeUseCase(
    private val cacheRepository: CacheRepository,
) {
    operator fun invoke(): Flow<Result<Long>> = cacheRepository.getCacheSize()
}
