package pion.tech.pionbase.feature.urlWallpaper

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.domain.usecase.home.DownloadImageToBitmapUseCase
import pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class UrlWallpaperViewModel(
    private val downloadImageToBitmapUseCase: DownloadImageToBitmapUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase
) : BaseViewModel<UrlWallpaperUiState, Nothing>(UrlWallpaperUiState()) {

    fun setWallpaperFromUrl(url: String) {
        setState { copy(isLoading = true) }
        handleApiCall(
            apiCall = { downloadImageToBitmapUseCase(url) },
            onSuccess = { bitmap ->
                handleApiCall(
                    apiCall = { setWallpaperUseCase(bitmap) },
                    onSuccess = { 
                        setState { copy(isLoading = false, isSuccess = true) }
                    },
                    onError = { throwable ->
                        setState { copy(isLoading = false, error = throwable) }
                    }
                )
            },
            onError = { throwable ->
                setState { copy(isLoading = false, error = throwable) }
            }
        )
    }
}

data class UrlWallpaperUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: Throwable? = null
)
