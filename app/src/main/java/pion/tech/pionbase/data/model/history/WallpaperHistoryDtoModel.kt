package pion.tech.pionbase.data.model.history

data class WallpaperHistoryDtoModel(
    val id: Long = 0,
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val title: String = "",
    val categoryName: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "VIEW"
)

fun WallpaperHistoryDtoModel.toPresentation() = WallpaperHistoryUIModel(
    id = this.id,
    imageUrl = this.imageUrl,
    thumbnailUrl = this.thumbnailUrl,
    title = this.title,
    categoryName = this.categoryName,
    timestamp = this.timestamp,
    type = this.type
)

fun WallpaperHistoryDtoModel.toEntity() = WallpaperHistoryEntity(
    id = this.id,
    imageUrl = this.imageUrl,
    thumbnailUrl = this.thumbnailUrl,
    title = this.title,
    categoryName = this.categoryName,
    timestamp = this.timestamp,
    type = this.type
)
