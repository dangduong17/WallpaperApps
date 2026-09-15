package pion.tech.pionbase.feature.home

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.CategoryUIModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.util.UiState

class HomeViewModel : BaseViewModel<HomeUiState, Nothing>(HomeUiState()) {

    init {
        getWallpapers()
        getCategories()
    }

    private fun getWallpapers() {
        // Dummy data for featured and top wallpapers
        val featured = listOf(
            WallpaperUIModel("Arts", "https://images.unsplash.com/photo-1541963463532-d68292c34b19"),
            WallpaperUIModel("Skyscraper", "https://images.unsplash.com/photo-1449156059437-d956f4d2a138"),
            WallpaperUIModel("Birds", "https://images.unsplash.com/photo-1444464666168-49d633b86797")
        )

        val topWallpapers = listOf(
            WallpaperUIModel("Fox", "https://images.unsplash.com/photo-1474511320721-9a6ee39b48f7"),
            WallpaperUIModel("Neon", "https://images.unsplash.com/photo-1511447333015-45b65e60f6d1"),
            WallpaperUIModel("Girl", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1"),
            WallpaperUIModel("Salad", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd"),
            WallpaperUIModel("Yoga", "https://images.unsplash.com/photo-1506126613408-eca07ce68773"),
            WallpaperUIModel("Bike", "https://images.unsplash.com/photo-1558981403-c5f9fdb12767")
        )

        setState { 
            copy(
                featuredUiState = UiState.Success(featured),
                topWallpaperUiState = UiState.Success(topWallpapers)
            )
        }
    }

    private fun getCategories() {
        val categories = listOf(
            CategoryUIModel("Animals", "https://images.unsplash.com/photo-1474511320721-9a6ee39b48f7"),
            CategoryUIModel("Fashion & Beauty", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1"),
            CategoryUIModel("Car & Vehicle", "https://images.unsplash.com/photo-1558981403-c5f9fdb12767"),
            CategoryUIModel("Natures", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b")
        )
        setState { copy(categoriesUiState = UiState.Success(categories)) }
    }
}

data class HomeUiState(
    val featuredUiState: UiState<List<WallpaperUIModel>> = UiState.None,
    val topWallpaperUiState: UiState<List<WallpaperUIModel>> = UiState.None,
    val categoriesUiState: UiState<List<CategoryUIModel>> = UiState.None,
)
