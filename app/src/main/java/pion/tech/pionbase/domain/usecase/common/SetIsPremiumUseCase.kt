package pion.tech.pionbase.domain.usecase.common

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

class SetIsPremiumUseCase(
    private val dataStoreRepository: DataStoreRepository,
) {
    operator fun invoke(isPremium: Boolean): Flow<Result<Unit>> =
        dataStoreRepository.setIsPremium(isPremium)
}
