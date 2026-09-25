package pion.tech.pionbase.feature.urlWallpaper

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.domain.usecase.home.DownloadImageToBitmapUseCase
import pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
import pion.tech.pionbase.util.handleApiCall

sealed class UrlWallpaperEvent {
    object SetWallpaperSuccess : UrlWallpaperEvent()
    data class SetWallpaperError(val throwable: Throwable) : UrlWallpaperEvent()
}

class UrlWallpaperViewModel(
    private val downloadImageToBitmapUseCase: DownloadImageToBitmapUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase,
) : BaseViewModel<UrlWallpaperUiState, UrlWallpaperEvent>(UrlWallpaperUiState()) {

    fun setWallpaperFromUrl(url: String) {
        setState { copy(isLoading = true) }
        handleApiCall(
            apiCall = { downloadImageToBitmapUseCase(url) },
            onSuccess = { bitmap ->
                handleApiCall(
                    apiCall = { setWallpaperUseCase(bitmap) },
                    onSuccess = { 
                        setState { copy(isLoading = false) }
                        launchMain { setEvent(UrlWallpaperEvent.SetWallpaperSuccess) }
                    },
                    onError = { throwable ->
                        setState { copy(isLoading = false) }
                        launchMain { setEvent(UrlWallpaperEvent.SetWallpaperError(throwable)) }
                    }
                )
            },
            onError = { throwable ->
                setState { copy(isLoading = false) }
                launchMain { setEvent(UrlWallpaperEvent.SetWallpaperError(throwable)) }
            }
        )
    }
}

data class UrlWallpaperUiState(
    val isLoading: Boolean = false,
)
