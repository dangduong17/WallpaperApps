package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.repository.wallpaperRepository.WallpaperRepository
import pion.tech.pionbase.domain.usecase.base.BaseUseCase
import pion.tech.pionbase.domain.usecase.home.DownloadImageToBitmapUseCase
import pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
import pion.tech.pionbase.util.Result

class SetRandomNextWallpaperUseCase(
    private val wallpaperRepository: WallpaperRepository,
    private val downloadImageToBitmapUseCase: DownloadImageToBitmapUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase,
) : BaseUseCase() {

    companion object {
        private var lastUsedUrl: String? = null
    }

    operator fun invoke(): Flow<Result<Unit>> = executeFlow {
        flow {
            var wallpapers: List<WallpaperDtoModel> = emptyList()

            val favoriteResult = wallpaperRepository.getFavoriteWallpapers().first()
            if (favoriteResult is Result.Success && favoriteResult.data.isNotEmpty()) {
                wallpapers = favoriteResult.data
            }

            if (wallpapers.isEmpty()) {
                val featuredResult = wallpaperRepository.getFeaturedWallpapers().first()
                if (featuredResult is Result.Success && featuredResult.data.isNotEmpty()) {
                    wallpapers = featuredResult.data
                }
            }

            if (wallpapers.isEmpty()) {
                val topResult = wallpaperRepository.getTopWallpapers().first()
                if (topResult is Result.Success && topResult.data.isNotEmpty()) {
                    wallpapers = topResult.data
                }
            }

            if (wallpapers.isEmpty()) {
                emit(Result.Error(Exception("No wallpapers available")))
                return@flow
            }

            val available = if (wallpapers.size > 1) {
                wallpapers.filter { it.safeImageUrl != lastUsedUrl }
            } else {
                wallpapers
            }

            val selected = available.random()
            lastUsedUrl = selected.safeImageUrl

            when (val bitmapResult = downloadImageToBitmapUseCase(selected.safeImageUrl).first()) {
                is Result.Success -> {
                    val setWallpaperResult = setWallpaperUseCase(bitmapResult.data).first()
                    emit(setWallpaperResult)
                }
                is Result.Error -> {
                    emit(Result.Error(bitmapResult.error))
                }
            }
        }
    }
}
