package pion.tech.pionbase.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Result

class SetAutoWallpaperSettingsUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    fun setEnabled(enabled: Boolean): Flow<Result<Unit>> = dataStoreRepository.setAutoWallpaperEnabled(enabled)
    fun setInterval(interval: Long): Flow<Result<Unit>> = dataStoreRepository.setAutoWallpaperInterval(interval)
}
