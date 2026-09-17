package pion.tech.pionbase.data.model.wallpaper

data class WallpaperDtoModel(
    val title: String,
    val imageUrl: String,
    val categoryName: String = "General"
)

fun WallpaperDtoModel.toPresentation() = WallpaperUIModel(
    title = this.title,
    imageUrl = this.imageUrl
)
