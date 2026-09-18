package pion.tech.pionbase.feature.favorite

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.data.model.wallpaper.toPresentation
import pion.tech.pionbase.domain.usecase.wallpaper.GetFavoriteWallpapersUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class FavoriteViewModel(
    private val getFavoriteWallpapersUseCase: GetFavoriteWallpapersUseCase
) : BaseViewModel<FavoriteUiState, Nothing>(FavoriteUiState()) {

    init {
        getFavorites()
    }

    private fun getFavorites() {
        handleApiCall(
            apiCall = { getFavoriteWallpapersUseCase() },
            onSuccess = { dtoList ->
                val favorites = dtoList.map { it.toPresentation() }
                setState { copy(favoritesUiState = UiState.Success(favorites)) }
            },
            onError = { throwable ->
                setState { copy(favoritesUiState = UiState.Error(throwable)) }
            }
        )
    }
}

data class FavoriteUiState(
    val favoritesUiState: UiState<List<WallpaperUIModel>> = UiState.None
)
