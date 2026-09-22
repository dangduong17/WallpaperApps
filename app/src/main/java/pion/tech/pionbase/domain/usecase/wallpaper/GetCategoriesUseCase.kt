package pion.tech.pionbase.domain.usecase.wallpaper

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.repository.wallpaperRepository.WallpaperRepository
import pion.tech.pionbase.util.Result

class GetCategoriesUseCase(
    private val repository: WallpaperRepository
) {
    operator fun invoke(): Flow<Result<List<CategoryDtoModel>>> =
        repository.getCategories()
}
