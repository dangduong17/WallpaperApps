package pion.tech.pionbase.data.model.wallpaper

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val imageUrl: String,
    val categoryName: String = "General",
    val isFeatured: Boolean = false,
    val isFavorite: Boolean = false
)

fun WallpaperEntity.toDto() = WallpaperDtoModel(
    title = this.title,
    imageUrl = this.imageUrl,
    categoryName = this.categoryName
)
