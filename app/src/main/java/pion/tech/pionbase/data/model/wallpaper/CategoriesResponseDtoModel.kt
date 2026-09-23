package pion.tech.pionbase.data.model.wallpaper

import com.google.gson.annotations.SerializedName

data class CategoriesResponseDtoModel(
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("categories")
    val categories: List<String> = emptyList(),
    @SerializedName("images")
    val images: List<WallpaperDtoModel> = emptyList()
)
