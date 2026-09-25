package pion.tech.pionbase.data.model.history

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel

@Parcelize
data class WallpaperHistoryUIModel(
    val id: Long = 0,
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val title: String = "",
    val categoryName: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = TYPE_VIEW
) : Parcelable {
    val resolvedThumbnailUrl: String
        get() {
            if (thumbnailUrl.isNotEmpty()) return thumbnailUrl
            return when {
                imageUrl.contains("/800/1200") -> imageUrl.replace("/800/1200", "/300/450")
                imageUrl.contains("w=800&h=1200") -> imageUrl.replace("w=800&h=1200", "w=300&h=450")
                else -> imageUrl
            }
        }

    fun toWallpaperUIModel(): WallpaperUIModel = WallpaperUIModel(
        title = title,
        imageUrl = imageUrl,
        thumbnailUrl = resolvedThumbnailUrl
    )

    companion object {
        const val TYPE_VIEW = "VIEW"
        const val TYPE_SET = "SET"
        const val TYPE_DOWNLOAD = "DOWNLOAD"
    }
}
