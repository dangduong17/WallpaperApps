package pion.tech.pionbase.feature.history

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.history.WallpaperHistoryUIModel
import pion.tech.pionbase.data.model.history.toPresentation
import pion.tech.pionbase.domain.usecase.history.ClearWallpaperHistoryUseCase
import pion.tech.pionbase.domain.usecase.history.GetDownloadAndSetHistoryUseCase
import pion.tech.pionbase.domain.usecase.history.GetRecentViewsUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class HistoryViewModel(
    private val getRecentViewsUseCase: GetRecentViewsUseCase,
    private val getDownloadAndSetHistoryUseCase: GetDownloadAndSetHistoryUseCase,
    private val clearWallpaperHistoryUseCase: ClearWallpaperHistoryUseCase
) : BaseViewModel<HistoryUiState, Nothing>(HistoryUiState()) {

    init {
        loadRecentViews()
        loadDownloadAndSetHistory()
    }

    fun selectTab(tabIndex: Int) {
        setState { copy(selectedTab = tabIndex) }
    }

    private fun loadRecentViews() {
        setState { copy(recentViewsState = UiState.Loading) }
        handleApiCall(
            apiCall = { getRecentViewsUseCase() },
            onSuccess = { list ->
                setState {
                    copy(
                        recentViewsState = UiState.Success(list.map { it.toPresentation() })
                    )
                }
            },
            onError = { throwable ->
                setState {
                    copy(recentViewsState = UiState.Error(throwable))
                }
            }
        )
    }

    private fun loadDownloadAndSetHistory() {
        setState { copy(downloadAndSetHistoryState = UiState.Loading) }
        handleApiCall(
            apiCall = { getDownloadAndSetHistoryUseCase() },
            onSuccess = { list ->
                setState {
                    copy(
                        downloadAndSetHistoryState = UiState.Success(list.map { it.toPresentation() })
                    )
                }
            },
            onError = { throwable ->
                setState {
                    copy(downloadAndSetHistoryState = UiState.Error(throwable))
                }
            }
        )
    }

    fun clearHistory() {
        val typeToClear = when (uiState.value.selectedTab) {
            0 -> WallpaperHistoryUIModel.TYPE_VIEW
            else -> "SET_DOWNLOAD"
        }
        setState { copy(isClearing = true) }
        handleApiCall(
            apiCall = { clearWallpaperHistoryUseCase(typeToClear) },
            onSuccess = {
                setState { copy(isClearing = false) }
            },
            onError = {
                setState { copy(isClearing = false) }
            }
        )
    }
}

data class HistoryUiState(
    val selectedTab: Int = 0, // 0 = Recent Views, 1 = Set/Download History
    val recentViewsState: UiState<List<WallpaperHistoryUIModel>> = UiState.None,
    val downloadAndSetHistoryState: UiState<List<WallpaperHistoryUIModel>> = UiState.None,
    val isClearing: Boolean = false
)
