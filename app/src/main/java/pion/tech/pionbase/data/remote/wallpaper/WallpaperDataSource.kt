package pion.tech.pionbase.data.remote.wallpaper

import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import pion.tech.pionbase.data.remote.ApiInterface

class WallpaperDataSource(private val apiInterface: ApiInterface) {
    suspend fun getFeaturedWallpapers(): List<WallpaperDtoModel> {
        return apiInterface.getFeaturedWallpapers().dataResponse
    }

    suspend fun getTopWallpapers(): List<WallpaperDtoModel> {
        return apiInterface.getTopWallpapers().dataResponse
    }

    suspend fun getCategories(): List<CategoryDtoModel> {
        return apiInterface.getWallpaperCategories().dataResponse
    }

    suspend fun getWallpapersByCategory(categoryName: String): List<WallpaperDtoModel> {
        return apiInterface.getWallpapersByCategory(categoryName).dataResponse
    }
}
