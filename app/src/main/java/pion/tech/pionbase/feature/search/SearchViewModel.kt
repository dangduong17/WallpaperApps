package pion.tech.pionbase.feature.search

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.data.model.wallpaper.toPresentation
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SearchWallpapersUseCase
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val searchWallpapersUseCase: SearchWallpapersUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel<SearchUiState, Nothing>(SearchUiState()) {

    private val searchTrigger = MutableSharedFlow<Pair<String, Boolean>>()
    private val suggestionTrigger = MutableSharedFlow<String>()
    private var cachedCategoryTitles: List<String>? = null

    init {
        observeSearchQuery()
        observeSuggestions()
    }

    private suspend fun getCategoryTitles(): List<String> {
        cachedCategoryTitles?.let { return it }
        return try {
            val result = getCategoriesUseCase().firstOrNull()
            if (result is Result.Success) {
                val titles = result.data.map { it.toPresentation().title }
                cachedCategoryTitles = titles
                titles
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun observeSuggestions() {
        launchIO {
            suggestionTrigger.collectLatest { query ->
                if (query.length >= 2) {
                    val allCategories = getCategoryTitles()
                    searchWallpapersUseCase(query).collect { wallpaperResult ->
                        val wallpaperTitles = if (wallpaperResult is Result.Success) {
                            wallpaperResult.data.map { it.safeTitle.replace(Regex("\\s\\d+$"), "").trim() }
                        } else {
                            emptyList()
                        }
                        
                        val suggestions = (allCategories + wallpaperTitles)
                            .filter { it.contains(query, true) }
                            .distinct()
                            .take(5)
                            
                        setState { copy(suggestions = suggestions) }
                    }
                } else {
                    setState { copy(suggestions = emptyList()) }
                }
            }
        }
    }

    private fun observeSearchQuery() {
        launchIO {
            searchTrigger.collectLatest { (query, isManual) ->
                if (isManual) {
                    performSearch(query)
                } else {
                    if (query.isEmpty()) {
                        performSearch("")
                    } else if (query.length >= 2) {
                        kotlinx.coroutines.delay(500)
                        performSearch(query)
                    }
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        launchIO {
            suggestionTrigger.emit(query)
            searchTrigger.emit(query to false)
        }
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
    val searchResultUiState: UiState<List<WallpaperUIModel>> = UiState.None,
    val suggestions: List<String> = emptyList()
)
