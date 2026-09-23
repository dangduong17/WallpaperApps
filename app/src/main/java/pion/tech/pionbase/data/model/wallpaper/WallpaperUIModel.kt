package pion.tech.pionbase.data.model.wallpaper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WallpaperUIModel(
    val title: String = "",
    val imageUrl: String = "",
    val thumbnailUrl: String = ""
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
}
