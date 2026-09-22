package pion.tech.pionbase.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

class GetAutoWallpaperSettingsUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    fun getEnabled(): Flow<Result<Boolean>> = dataStoreRepository.getAutoWallpaperEnabled()
    fun getInterval(): Flow<Result<Long>> = dataStoreRepository.getAutoWallpaperInterval()
}
