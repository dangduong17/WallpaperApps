package pion.tech.pionbase.data.model.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpaper_history")
data class WallpaperHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUrl: String,
    val thumbnailUrl: String = "",
    val title: String = "",
    val categoryName: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String // "VIEW", "SET", "DOWNLOAD"
)

fun WallpaperHistoryEntity.toDto() = WallpaperHistoryDtoModel(
    id = this.id,
    imageUrl = this.imageUrl,
    thumbnailUrl = this.thumbnailUrl,
    title = this.title,
    categoryName = this.categoryName,
    timestamp = this.timestamp,
    type = this.type
)
