package pion.tech.pionbase.data.model.wallpaper

import com.google.gson.annotations.SerializedName

data class WallpaperDtoModel(
    @SerializedName("title")
    val title: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("categoryName")
    val categoryName: String = "General",
    @SerializedName("id")
    val id: Int? = null,
)

fun WallpaperDtoModel.toPresentation() = WallpaperUIModel(
    title = this.title,
    imageUrl = this.imageUrl,
)

fun WallpaperDtoModel.toEntity(isFeatured: Boolean = false) = WallpaperEntity(
    title = this.title,
    imageUrl = this.imageUrl,
    categoryName = this.categoryName,
    isFeatured = isFeatured,
)
