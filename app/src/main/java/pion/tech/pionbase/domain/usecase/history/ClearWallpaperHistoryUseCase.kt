package pion.tech.pionbase.domain.usecase.history

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pion.tech.pionbase.data.repository.historyRepository.HistoryRepository
import pion.tech.pionbase.util.Result

class ClearWallpaperHistoryUseCase(
    private val repository: HistoryRepository
) {
    operator fun invoke(type: String? = null): Flow<Result<Unit>> = flow {
        emit(repository.clearHistory(type))
    }
}
