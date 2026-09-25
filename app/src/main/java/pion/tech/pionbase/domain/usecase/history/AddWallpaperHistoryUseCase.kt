package pion.tech.pionbase.domain.usecase.history

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pion.tech.pionbase.data.repository.historyRepository.HistoryRepository
import pion.tech.pionbase.util.Result

class AddWallpaperHistoryUseCase(
    private val repository: HistoryRepository
) {
    operator fun invoke(
        imageUrl: String,
        thumbnailUrl: String = "",
        title: String = "",
        categoryName: String = "General",
        type: String = "VIEW"
    ): Flow<Result<Unit>> = flow {
        emit(repository.addHistory(imageUrl, thumbnailUrl, title, categoryName, type))
    }
}
