package pion.tech.pionbase.data.model.wallpaper

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val imageUrl: String
)

fun CategoryEntity.toDto() = CategoryDtoModel(
    title = this.title,
    imageUrl = this.imageUrl
)
