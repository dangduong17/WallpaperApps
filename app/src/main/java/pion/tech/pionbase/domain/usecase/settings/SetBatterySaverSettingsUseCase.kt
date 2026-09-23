package pion.tech.pionbase.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

class SetBatterySaverSettingsUseCase(
    private val dataStoreRepository: DataStoreRepository,
) {
    operator fun invoke(enabled: Boolean): Flow<Result<Unit>> = dataStoreRepository.setBatterySaverEnabled(enabled)
}
