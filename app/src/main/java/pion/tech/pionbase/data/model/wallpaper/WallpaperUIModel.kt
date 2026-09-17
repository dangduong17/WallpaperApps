package pion.tech.pionbase.data.model.wallpaper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WallpaperUIModel(
    val title: String,
    val imageUrl: String
) : Parcelable
