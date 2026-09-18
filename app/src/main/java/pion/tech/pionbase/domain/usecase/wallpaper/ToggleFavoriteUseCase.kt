package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.util.Result

class ToggleFavoriteUseCase(
    private val repository: WallpaperRepository
) {
    operator fun invoke(imageUrl: String): Flow<Result<Unit>> = flow {
        emit(repository.toggleFavorite(imageUrl))
    }
}
