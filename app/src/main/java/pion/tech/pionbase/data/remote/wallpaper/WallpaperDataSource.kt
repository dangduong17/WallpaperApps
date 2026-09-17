package pion.tech.pionbase.data.remote.wallpaper

import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel

class WallpaperDataSource {
    fun getFeaturedWallpapers(): List<WallpaperDtoModel> {
        return listOf(
            WallpaperDtoModel("Nature Art", "https://picsum.photos/seed/nart1/800/1200", "Nature"),
            WallpaperDtoModel("City Tech", "https://picsum.photos/seed/ctech1/800/1200", "Technology"),
            WallpaperDtoModel("Ocean View", "https://picsum.photos/seed/ocean1/800/1200", "Nature"),
            WallpaperDtoModel("Sky High", "https://picsum.photos/seed/sky1/800/1200", "Nature"),
            WallpaperDtoModel("Urban Life", "https://picsum.photos/seed/urban1/800/1200", "Architecture")
        )
    }

    fun getTopWallpapers(): List<WallpaperDtoModel> {
        return listOf(
            WallpaperDtoModel("Abstract 1", "https://picsum.photos/seed/abs1/800/1200", "Abstract"),
            WallpaperDtoModel("Abstract 2", "https://picsum.photos/seed/abs2/800/1200", "Abstract"),
            WallpaperDtoModel("Animal 1", "https://picsum.photos/seed/ani1/800/1200", "Animals"),
            WallpaperDtoModel("Animal 2", "https://picsum.photos/seed/ani2/800/1200", "Animals"),
            WallpaperDtoModel("Arch 1", "https://picsum.photos/seed/arch1/800/1200", "Architecture"),
            WallpaperDtoModel("Arch 2", "https://picsum.photos/seed/arch2/800/1200", "Architecture"),
            WallpaperDtoModel("Food 1", "https://picsum.photos/seed/food1/800/1200", "Food"),
            WallpaperDtoModel("Food 2", "https://picsum.photos/seed/food2/800/1200", "Food")
        )
    }

    fun getCategories(): List<CategoryDtoModel> {
        return listOf(
            CategoryDtoModel("Animals", "https://picsum.photos/seed/cat_ani/500/300"),
            CategoryDtoModel("Architecture", "https://picsum.photos/seed/cat_arc/500/300"),
            CategoryDtoModel("Nature", "https://picsum.photos/seed/cat_nat/500/300"),
            CategoryDtoModel("Technology", "https://picsum.photos/seed/cat_tec/500/300")
        )
    }
}
