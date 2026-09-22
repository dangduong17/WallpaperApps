package pion.tech.pionbase.domain.usecase.language

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

class GetLanguageUseCase(
    private val dataStoreRepository: DataStoreRepository,
) {
    operator fun invoke(): Flow<Result<String>> = dataStoreRepository.getLanguage()
}
