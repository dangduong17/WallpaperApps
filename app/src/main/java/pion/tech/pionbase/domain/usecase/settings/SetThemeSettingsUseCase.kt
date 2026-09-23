package pion.tech.pionbase.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

class SetThemeSettingsUseCase(
    private val dataStoreRepository: DataStoreRepository,
) {
    fun setThemeMode(themeMode: Int): Flow<Result<Unit>> = dataStoreRepository.setThemeMode(themeMode)
    fun setDynamicColorEnabled(enabled: Boolean): Flow<Result<Unit>> = dataStoreRepository.setDynamicColorEnabled(enabled)
}
