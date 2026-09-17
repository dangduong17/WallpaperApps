package pion.tech.pionbase.feature.wallpaperDetail

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.domain.usecase.wallpaper.IsFavoriteWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.ToggleFavoriteUseCase
import pion.tech.pionbase.util.handleApiCall

class WallpaperDetailViewModel(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteWallpaperUseCase: IsFavoriteWallpaperUseCase
) : BaseViewModel<WallpaperDetailUiState, Nothing>(WallpaperDetailUiState()) {
    fun setWallpaper(item: WallpaperUIModel) {
        setState { copy(wallpaper = item) }
        checkFavoriteStatus(item.imageUrl)
    }

    private fun checkFavoriteStatus(url: String) {
        handleApiCall(
            apiCall = { isFavoriteWallpaperUseCase(url) },
            onSuccess = { isFav ->
                setState { copy(isFavorite = isFav) }
            }
        )
    }

    fun toggleFavorite() {
        val currentWallpaper = uiState.value.wallpaper ?: return
        handleApiCall(
            apiCall = { toggleFavoriteUseCase(currentWallpaper.imageUrl) },
            onSuccess = {
                setState { copy(isFavorite = !isFavorite) }
            }
        )
    }
}

data class WallpaperDetailUiState(
    val wallpaper: WallpaperUIModel? = null,
    val isFavorite: Boolean = false
)
