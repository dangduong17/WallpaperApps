package pion.tech.pionbase.data.remote.wallpaper

import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel

class WallpaperDataSource {
    fun getFeaturedWallpapers(): List<WallpaperDtoModel> {
        return listOf(
            WallpaperDtoModel("Green Valley", "https://picsum.photos/seed/nv1/800/1200", "Nature"),
            WallpaperDtoModel("Modern Office", "https://picsum.photos/seed/to1/800/1200", "Technology"),
            WallpaperDtoModel("Deep Sea", "https://picsum.photos/seed/ns1/800/1200", "Nature"),
            WallpaperDtoModel("Blue Galaxy", "https://picsum.photos/seed/ag1/800/1200", "Abstract"),
            WallpaperDtoModel("Glass Tower", "https://picsum.photos/seed/at1/800/1200", "Architecture")
        )
    }

    fun getTopWallpapers(): List<WallpaperDtoModel> {
        return listOf(
            // Nature - 6 images
            WallpaperDtoModel("Nature 1", "https://picsum.photos/seed/nature1/800/1200", "Nature"),
            WallpaperDtoModel("Nature 2", "https://picsum.photos/seed/nature2/800/1200", "Nature"),
            WallpaperDtoModel("Nature 3", "https://picsum.photos/seed/nature3/800/1200", "Nature"),
            WallpaperDtoModel("Nature 4", "https://picsum.photos/seed/nature4/800/1200", "Nature"),
            WallpaperDtoModel("Nature 5", "https://picsum.photos/seed/nature5/800/1200", "Nature"),
            WallpaperDtoModel("Nature 6", "https://picsum.photos/seed/nature6/800/1200", "Nature"),

            // Technology - 6 images
            WallpaperDtoModel("Tech 1", "https://picsum.photos/seed/tech1/800/1200", "Technology"),
            WallpaperDtoModel("Tech 2", "https://picsum.photos/seed/tech2/800/1200", "Technology"),
            WallpaperDtoModel("Tech 3", "https://picsum.photos/seed/tech3/800/1200", "Technology"),
            WallpaperDtoModel("Tech 4", "https://picsum.photos/seed/tech4/800/1200", "Technology"),
            WallpaperDtoModel("Tech 5", "https://picsum.photos/seed/tech5/800/1200", "Technology"),
            WallpaperDtoModel("Tech 6", "https://picsum.photos/seed/tech6/800/1200", "Technology"),

            // Animals - 6 images
            WallpaperDtoModel("Animal 1", "https://picsum.photos/seed/animal1/800/1200", "Animals"),
            WallpaperDtoModel("Animal 2", "https://picsum.photos/seed/animal2/800/1200", "Animals"),
            WallpaperDtoModel("Animal 3", "https://picsum.photos/seed/animal3/800/1200", "Animals"),
            WallpaperDtoModel("Animal 4", "https://picsum.photos/seed/animal4/800/1200", "Animals"),
            WallpaperDtoModel("Animal 5", "https://picsum.photos/seed/animal5/800/1200", "Animals"),
            WallpaperDtoModel("Animal 6", "https://picsum.photos/seed/animal6/800/1200", "Animals"),

            // Architecture - 6 images
            WallpaperDtoModel("Arch 1", "https://picsum.photos/seed/arch1/800/1200", "Architecture"),
            WallpaperDtoModel("Arch 2", "https://picsum.photos/seed/arch2/800/1200", "Architecture"),
            WallpaperDtoModel("Arch 3", "https://picsum.photos/seed/arch3/800/1200", "Architecture"),
            WallpaperDtoModel("Arch 4", "https://picsum.photos/seed/arch4/800/1200", "Architecture"),
            WallpaperDtoModel("Arch 5", "https://picsum.photos/seed/arch5/800/1200", "Architecture"),
            WallpaperDtoModel("Arch 6", "https://picsum.photos/seed/arch6/800/1200", "Architecture"),

            // Abstract - 6 images
            WallpaperDtoModel("Abstract 1", "https://picsum.photos/seed/abs1/800/1200", "Abstract"),
            WallpaperDtoModel("Abstract 2", "https://picsum.photos/seed/abs2/800/1200", "Abstract"),
            WallpaperDtoModel("Abstract 3", "https://picsum.photos/seed/abs3/800/1200", "Abstract"),
            WallpaperDtoModel("Abstract 4", "https://picsum.photos/seed/abs4/800/1200", "Abstract"),
            WallpaperDtoModel("Abstract 5", "https://picsum.photos/seed/abs5/800/1200", "Abstract"),
            WallpaperDtoModel("Abstract 6", "https://picsum.photos/seed/abs6/800/1200", "Abstract")
        )
    }

    fun getCategories(): List<CategoryDtoModel> {
        return listOf(
            CategoryDtoModel("Animals", "https://picsum.photos/seed/c_anim/500/300"),
            CategoryDtoModel("Architecture", "https://picsum.photos/seed/c_arch/500/300"),
            CategoryDtoModel("Nature", "https://picsum.photos/seed/c_nat/500/300"),
            CategoryDtoModel("Technology", "https://picsum.photos/seed/c_tech/500/300"),
            CategoryDtoModel("Abstract", "https://picsum.photos/seed/c_abs/500/300")
        )
    }
}
