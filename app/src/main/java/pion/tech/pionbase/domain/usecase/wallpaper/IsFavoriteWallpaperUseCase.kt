package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.wallpaperRepository.WallpaperRepository
import pion.tech.pionbase.util.Result

class IsFavoriteWallpaperUseCase(
    private val repository: WallpaperRepository
) {
    operator fun invoke(url: String): Flow<Result<Boolean>> =
        repository.isFavorite(url)
}
