package pion.tech.pionbase.feature.search

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.data.model.wallpaper.toPresentation
import pion.tech.pionbase.domain.usecase.wallpaper.SearchWallpapersUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class SearchViewModel(
    private val searchWallpapersUseCase: SearchWallpapersUseCase
) : BaseViewModel<SearchUiState, Nothing>(SearchUiState()) {

    fun search(query: String) {
        if (query.isEmpty()) {
            setState { copy(searchResultUiState = UiState.None) }
            return
        }

        setState { copy(searchResultUiState = UiState.Loading) }
        handleApiCall(
            apiCall = { searchWallpapersUseCase(query) },
            onSuccess = { dtoList ->
                val result = dtoList.map { it.toPresentation() }
                setState { copy(searchResultUiState = UiState.Success(result)) }
            },
            onError = { throwable ->
                setState { copy(searchResultUiState = UiState.Error(throwable)) }
            }
        )
    }
}

data class SearchUiState(
    val searchResultUiState: UiState<List<WallpaperUIModel>> = UiState.None
)
