package pion.tech.pionbase.domain.usecase.language

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

class SetLanguageUseCase(
    private val dataStoreRepository: DataStoreRepository,
) {
    operator fun invoke(localeCode: String): Flow<Result<Unit>> =
        dataStoreRepository.setLanguage(localeCode)
}
