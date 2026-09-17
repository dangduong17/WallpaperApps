package pion.tech.pionbase.data.remote.wallpaper

import pion.tech.pionbase.data.model.wallpaper.CategoryDtoModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperDtoModel

class WallpaperDataSource {
    fun getFeaturedWallpapers(): List<WallpaperDtoModel> {
        return listOf(
            WallpaperDtoModel("Arts", "https://images.unsplash.com/photo-1541963463532-d68292c34b19"),
            WallpaperDtoModel("Skyscraper", "https://images.unsplash.com/photo-1449156059437-d956f4d2a138"),
            WallpaperDtoModel("Birds", "https://images.unsplash.com/photo-1444464666168-49d633b86797")
        )
    }

    fun getTopWallpapers(): List<WallpaperDtoModel> {
        return listOf(
            WallpaperDtoModel("Fox", "https://images.unsplash.com/photo-1474511320721-9a6ee39b48f7"),
            WallpaperDtoModel("Neon", "https://images.unsplash.com/photo-1511447333015-45b65e60f6d1"),
            WallpaperDtoModel("Girl", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1"),
            WallpaperDtoModel("Salad", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd"),
            WallpaperDtoModel("Yoga", "https://images.unsplash.com/photo-1506126613408-eca07ce68773"),
            WallpaperDtoModel("Bike", "https://images.unsplash.com/photo-1558981403-c5f9fdb12767")
        )
    }

    fun getCategories(): List<CategoryDtoModel> {
        return listOf(
            CategoryDtoModel("Animals", "https://images.unsplash.com/photo-1474511320721-9a6ee39b48f7"),
            CategoryDtoModel("Fashion & Beauty", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1"),
            CategoryDtoModel("Car & Vehicle", "https://images.unsplash.com/photo-1558981403-c5f9fdb12767"),
            CategoryDtoModel("Natures", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b")
        )
    }
}
