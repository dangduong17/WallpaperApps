package pion.tech.pionbase.feature.wallpaperDetail

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel

class WallpaperDetailViewModel : BaseViewModel<WallpaperDetailUiState, Nothing>(WallpaperDetailUiState()) {
    fun setWallpaper(item: WallpaperUIModel) {
        setState { copy(wallpaper = item) }
    }
}

data class WallpaperDetailUiState(
    val wallpaper: WallpaperUIModel? = null,
    val isFavorite: Boolean = false
)
