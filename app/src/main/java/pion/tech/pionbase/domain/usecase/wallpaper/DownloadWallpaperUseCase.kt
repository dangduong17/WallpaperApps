package pion.tech.pionbase.domain.usecase.wallpaper

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.util.Result

class DownloadWallpaperUseCase(
    private val wallpaperRepository: WallpaperRepository,
) {
    suspend operator fun invoke(url: String): Flow<Result<Uri>> =
        wallpaperRepository.downloadWallpaper(url)
}
