package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.util.Result

class GetFavoriteWallpapersUseCase(
    private val repository: WallpaperRepository
) {
    operator fun invoke(): Flow<Result<List<WallpaperDtoModel>>> =
        repository.getFavoriteWallpapers()
}
