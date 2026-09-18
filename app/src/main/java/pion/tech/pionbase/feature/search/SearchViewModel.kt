package pion.tech.pionbase.feature.search

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.data.model.wallpaper.toPresentation
import pion.tech.pionbase.domain.usecase.wallpaper.SearchWallpapersUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val searchWallpapersUseCase: SearchWallpapersUseCase
) : BaseViewModel<SearchUiState, Nothing>(SearchUiState()) {

    private val searchTrigger = MutableSharedFlow<Pair<String, Boolean>>() // Pair<Query, IsManual>

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        launchIO {
            searchTrigger
                .collectLatest { (query, isManual) ->
                    if (isManual) {
                        performSearch(query)
                    } else {
                        // Debounce only for real-time text changes
                        // filter empty queries to clear results immediately
                        if (query.isEmpty()) {
                            performSearch("")
                        } else if (query.length >= 2) {
                            kotlinx.coroutines.delay(700)
                            performSearch(query)
                        }
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        launchIO { searchTrigger.emit(query to false) }
    }

    fun searchNow(query: String) {
        launchIO { searchTrigger.emit(query to true) }
    }

    private fun performSearch(query: String) {
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
