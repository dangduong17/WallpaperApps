package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.repository.wallpaper.WallpaperRepository
import pion.tech.pionbase.util.Result

class SearchWallpapersUseCase(
    private val repository: WallpaperRepository
) {
    operator fun invoke(query: String): Flow<Result<List<WallpaperDtoModel>>> =
        repository.searchWallpapers(query)
}
