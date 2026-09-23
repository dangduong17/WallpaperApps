package pion.tech.pionbase.data.remote

import pion.tech.pionbase.data.model.ApiObjectResponseData
import pion.tech.pionbase.data.model.appCategory.AppCategoryDtoModel
import pion.tech.pionbase.data.model.template.TemplateResponseDtoModel
import pion.tech.pionbase.data.model.wallpaper.CategoriesResponseDtoModel
import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("https://raw.githubusercontent.com/dangduong17/DataD/refs/heads/main/categories.json")
    suspend fun getCategoriesData(): CategoriesResponseDtoModel

    @GET("api/v5.0/public/categories?app_id=56ba3e1f-27a4-4acd-b420-1b33600ac495")
    suspend fun getAppCategory(): ApiObjectResponseData<List<AppCategoryDtoModel>>

    @GET("api/v5.0/public/items/get-all?region_code=%7Bregion_code%7D")
    suspend fun getAllTemplate(
        @Query("category_id") categoryId: String,
    ): ApiObjectResponseData<List<TemplateResponseDtoModel>>

    @GET("api/v1.0/public/wallpapers/featured")
    suspend fun getFeaturedWallpapers(): ApiObjectResponseData<List<WallpaperDtoModel>>

    @GET("api/v1.0/public/wallpapers/top")
    suspend fun getTopWallpapers(): ApiObjectResponseData<List<WallpaperDtoModel>>

    @GET("api/v1.0/public/wallpapers/categories")
    suspend fun getWallpaperCategories(): ApiObjectResponseData<List<CategoryDtoModel>>

    @GET("api/v1.0/public/wallpapers")
    suspend fun getWallpapersByCategory(
        @Query("category") categoryName: String,
    ): ApiObjectResponseData<List<WallpaperDtoModel>>
}
