package pion.tech.pionbase.domain.usecase.history

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.history.WallpaperHistoryDtoModel
import pion.tech.pionbase.data.repository.historyRepository.HistoryRepository
import pion.tech.pionbase.util.Result

class GetDownloadAndSetHistoryUseCase(
    private val repository: HistoryRepository
) {
    operator fun invoke(): Flow<Result<List<WallpaperHistoryDtoModel>>> =
        repository.getDownloadAndSetHistory()
}
