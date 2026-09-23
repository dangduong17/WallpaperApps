package pion.tech.pionbase.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

class GetThemeSettingsUseCase(
    private val dataStoreRepository: DataStoreRepository,
) {
    fun getThemeMode(): Flow<Result<Int>> = dataStoreRepository.getThemeMode()
    fun getDynamicColorEnabled(): Flow<Result<Boolean>> = dataStoreRepository.getDynamicColorEnabled()
}
