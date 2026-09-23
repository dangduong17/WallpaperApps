package pion.tech.pionbase.data.model.wallpaper

import com.google.gson.annotations.SerializedName

data class WallpaperDtoModel(
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("imageUrl")
    val imageUrl: String? = "",
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String? = "",
    @SerializedName("categoryName")
    val categoryName: String? = "General",
    @SerializedName("id")
    val id: Int? = null,
) {
    val safeTitle: String
        get() = title ?: ""

    val safeImageUrl: String
        get() = imageUrl ?: ""

    val resolvedThumbnailUrl: String
        get() {
            val thumb = thumbnailUrl ?: ""
            if (thumb.isNotEmpty()) return thumb
            val url = safeImageUrl
            return when {
                url.contains("/800/1200") -> url.replace("/800/1200", "/300/450")
                url.contains("w=800&h=1200") -> url.replace("w=800&h=1200", "w=300&h=450")
                else -> url
            }
        }
}

fun WallpaperDtoModel.toPresentation() = WallpaperUIModel(
    title = this.safeTitle,
    imageUrl = this.safeImageUrl,
    thumbnailUrl = this.resolvedThumbnailUrl,
)

fun WallpaperDtoModel.toEntity(isFeatured: Boolean = false) = WallpaperEntity(
    title = this.safeTitle,
    imageUrl = this.safeImageUrl,
    thumbnailUrl = this.resolvedThumbnailUrl,
    categoryName = this.categoryName ?: "General",
    isFeatured = isFeatured,
)
