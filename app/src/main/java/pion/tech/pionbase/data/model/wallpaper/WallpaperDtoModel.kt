package pion.tech.pionbase.data.model.wallpaper

data class WallpaperDtoModel(
    val title: String,
    val imageUrl: String
)

fun WallpaperDtoModel.toPresentation() = WallpaperUIModel(
    title = this.title,
    imageUrl = this.imageUrl
)
