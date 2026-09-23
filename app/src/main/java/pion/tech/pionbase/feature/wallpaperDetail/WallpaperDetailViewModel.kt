package pion.tech.pionbase.feature.wallpaperDetail

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.domain.usecase.home.DownloadImageToBitmapUseCase
import pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.DownloadWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.IsFavoriteWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.ToggleFavoriteUseCase
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.handleApiCall
import timber.log.Timber

private const val ACTION_DELAY_MS = 400L

class WallpaperDetailViewModel(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteWallpaperUseCase: IsFavoriteWallpaperUseCase,
    private val downloadWallpaperUseCase: DownloadWallpaperUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase,
    private val downloadImageToBitmapUseCase: DownloadImageToBitmapUseCase,
) : BaseViewModel<WallpaperDetailUiState, WallpaperDetailEvent>(WallpaperDetailUiState()) {

    fun downloadWallpaper() {
        val currentWallpaper = uiState.value.wallpaper ?: return
        setState { copy(isLoading = true) }
        handleApiCall(
            apiCall = { downloadWallpaperUseCase(currentWallpaper.imageUrl) },
            onSuccess = { uri ->
                launchMain {
                    kotlinx.coroutines.delay(ACTION_DELAY_MS)
                    setState { copy(isLoading = false) }
                    setEvent(WallpaperDetailEvent.DownloadSuccess(uri))
                }
            },
            onError = { throwable ->
                launchMain {
                    kotlinx.coroutines.delay(ACTION_DELAY_MS)
                    setState { copy(isLoading = false) }
                    setEvent(WallpaperDetailEvent.DownloadError(throwable))
                }
            }
        )
    }

    fun setWallpaper(item: WallpaperUIModel) {
        val isGif = item.imageUrl.endsWith(".gif", ignoreCase = true)
        Timber.d("DEBUG: URL=${item.imageUrl}, isGif=$isGif")
        setState { copy(wallpaper = item, isGif = isGif) }
        checkFavoriteStatus(item.imageUrl)
    }

    fun applyWallpaper(uri: android.net.Uri, which: Int) {
        executeApplyWallpaper { setWallpaperUseCase(uri, which) }
    }

    fun applyWallpaper(bitmap: android.graphics.Bitmap, which: Int) {
        executeApplyWallpaper { setWallpaperUseCase(bitmap, which) }
    }

    fun applyWallpaperFromUrl(url: String, which: Int) {
        setState { copy(isSettingWallpaper = true) }
        launchIO(
            onError = { throwable ->
                launchMain {
                    setState { copy(isSettingWallpaper = false) }
                    setEvent(WallpaperDetailEvent.SetWallpaperError(throwable))
                }
            }
        ) {
            try {
                downloadImageToBitmapUseCase(url).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            executeApplyWallpaper { setWallpaperUseCase(result.data, which) }
                        }
                        is Result.Error -> {
                            launchMain {
                                setState { copy(isSettingWallpaper = false) }
                                setEvent(WallpaperDetailEvent.SetWallpaperError(result.error))
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                launchMain {
                    setState { copy(isSettingWallpaper = false) }
                    setEvent(WallpaperDetailEvent.SetWallpaperError(if (e is Exception) e else Exception(e)))
                }
            }
        }
    }

    private fun executeApplyWallpaper(apiCall: () -> Flow<Result<Unit>>) {
        setState { copy(isSettingWallpaper = true) }
        launchIO(
            onError = { throwable ->
                launchMain {
                    setState { copy(isSettingWallpaper = false) }
                    setEvent(WallpaperDetailEvent.SetWallpaperError(throwable))
                }
            }
        ) {
            try {
                apiCall().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            launchMain {
                                kotlinx.coroutines.delay(ACTION_DELAY_MS)
                                setEvent(WallpaperDetailEvent.SetWallpaperSuccess)
                            }
                        }
                        is Result.Error -> {
                            launchMain {
                                kotlinx.coroutines.delay(ACTION_DELAY_MS)
                                setEvent(WallpaperDetailEvent.SetWallpaperError(result.error))
                            }
                        }
                    }
                }
            } finally {
                // Đảm bảo tắt loading trong mọi trường hợp (thành công, thất bại, hoặc ngoại lệ)
                launchMain {
                    setState { copy(isSettingWallpaper = false) }
                }
            }
        }
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
                launchMain { setState { copy(isFavorite = !isFavorite) } }
            }
        )
    }
}

data class WallpaperDetailUiState(
    val wallpaper: WallpaperUIModel? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isSettingWallpaper: Boolean = false,
    val isGif: Boolean = false
)

sealed class WallpaperDetailEvent {
    data class DownloadSuccess(val uri: android.net.Uri) : WallpaperDetailEvent()
    data class DownloadError(val throwable: Throwable) : WallpaperDetailEvent()
    object SetWallpaperSuccess : WallpaperDetailEvent()
    data class SetWallpaperError(val throwable: Throwable) : WallpaperDetailEvent()
}
