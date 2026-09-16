package pion.tech.pionbase.feature.home

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.CategoryUIModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.data.model.wallpaper.toPresentation
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFeaturedWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetTopWallpapersUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class HomeViewModel(
    private val getFeaturedWallpapersUseCase: GetFeaturedWallpapersUseCase,
    private val getTopWallpapersUseCase: GetTopWallpapersUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel<HomeUiState, Nothing>(HomeUiState()) {

    init {
        getFeaturedWallpapers()
        getTopWallpapers()
        getCategories()
    }

    private fun getFeaturedWallpapers() {
        handleApiCall(
            apiCall = { getFeaturedWallpapersUseCase() },
            onSuccess = { dtoList ->
                val featured = dtoList.map { it.toPresentation() }
                setState { copy(featuredUiState = UiState.Success(featured)) }
            },
            onError = { throwable ->
                setState { copy(featuredUiState = UiState.Error(throwable)) }
            },
        )
    }

    private fun getTopWallpapers() {
        handleApiCall(
            apiCall = { getTopWallpapersUseCase() },
            onSuccess = { dtoList ->
                val topWallpapers = dtoList.map { it.toPresentation() }
                setState { copy(topWallpaperUiState = UiState.Success(topWallpapers)) }
            },
            onError = { throwable ->
                setState { copy(topWallpaperUiState = UiState.Error(throwable)) }
            },
        )
    }

    private fun getCategories() {
        handleApiCall(
            apiCall = { getCategoriesUseCase() },
            onSuccess = { dtoList ->
                val categories = dtoList.map { it.toPresentation() }
                setState { copy(categoriesUiState = UiState.Success(categories)) }
            },
            onError = { throwable ->
                setState { copy(categoriesUiState = UiState.Error(throwable)) }
            },
        )
    }
}

data class HomeUiState(
    val featuredUiState: UiState<List<WallpaperUIModel>> = UiState.None,
    val topWallpaperUiState: UiState<List<WallpaperUIModel>> = UiState.None,
    val categoriesUiState: UiState<List<CategoryUIModel>> = UiState.None,
)
