package pion.tech.pionbase.feature.categoryDetail

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.data.model.wallpaper.toPresentation
import pion.tech.pionbase.domain.usecase.wallpaper.GetWallpapersByCategoryUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class CategoryDetailViewModel(
    private val getWallpapersByCategoryUseCase: GetWallpapersByCategoryUseCase
) : BaseViewModel<CategoryDetailUiState, Nothing>(CategoryDetailUiState()) {

    fun getWallpapersByCategory(categoryName: String) {
        setState { copy(wallpapersUiState = UiState.Loading) }
        handleApiCall(
            apiCall = { getWallpapersByCategoryUseCase(categoryName) },
            onSuccess = { dtoList ->
                val result = dtoList.map { it.toPresentation() }
                setState { copy(wallpapersUiState = UiState.Success(result)) }
            },
            onError = { throwable ->
                setState { copy(wallpapersUiState = UiState.Error(throwable)) }
            }
        )
    }
}

data class CategoryDetailUiState(
    val wallpapersUiState: UiState<List<WallpaperUIModel>> = UiState.None
)
