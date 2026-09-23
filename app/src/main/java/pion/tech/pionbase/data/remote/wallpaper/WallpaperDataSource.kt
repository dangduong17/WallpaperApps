package pion.tech.pionbase.data.remote.wallpaper

import pion.tech.pionbase.data.model.wallpaper.CategoriesResponseDtoModel
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.remote.ApiInterface

class WallpaperDataSource(private val apiInterface: ApiInterface) {

    suspend fun getCategoriesData(): CategoriesResponseDtoModel {
        return apiInterface.getCategoriesData()
    }

    suspend fun getFeaturedWallpapers(): List<WallpaperDtoModel> {
        return apiInterface.getCategoriesData().images
    }

    suspend fun getTopWallpapers(): List<WallpaperDtoModel> {
        return apiInterface.getCategoriesData().images
    }

    suspend fun getCategories(): List<CategoryDtoModel> {
        val response = apiInterface.getCategoriesData()
        val images = response.images
        return response.categories.map { categoryName ->
            val imageUrl = images.firstOrNull { it.categoryName.equals(categoryName, ignoreCase = true) }?.imageUrl ?: ""
            CategoryDtoModel(title = categoryName, imageUrl = imageUrl)
        }
    }

    suspend fun getWallpapersByCategory(categoryName: String): List<WallpaperDtoModel> {
        val response = apiInterface.getCategoriesData()
        return response.images.filter { it.categoryName.equals(categoryName, ignoreCase = true) }
    }
}
