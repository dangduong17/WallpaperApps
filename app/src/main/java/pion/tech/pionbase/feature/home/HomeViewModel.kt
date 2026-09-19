package pion.tech.pionbase.feature.home

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.wallpaper.CategoryUIModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.data.model.wallpaper.toPresentation
import pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFeaturedWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetTopWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetWallpapersByCategoryUseCase
import pion.tech.pionbase.feature.home.adapter.FilterUIModel
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class HomeViewModel(
    private val getFeaturedWallpapersUseCase: GetFeaturedWallpapersUseCase,
    private val getTopWallpapersUseCase: GetTopWallpapersUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getWallpapersByCategoryUseCase: GetWallpapersByCategoryUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase
) : BaseViewModel<HomeUiState, Nothing>(HomeUiState()) {

    init {
        getFeaturedWallpapers()
        getTopWallpapers()
        getCategories()
    }

    private fun getFeaturedWallpapers() {
        handleApiCall(
            apiCall = { getFeaturedWallpapersUseCase() },
            onSuccess = { dtoList ->
                val featured = dtoList.map { it.toPresentation() }
                setState { copy(featuredUiState = UiState.Success(featured)) }
            },
            onError = { throwable ->
                setState { copy(featuredUiState = UiState.Error(throwable)) }
            },
        )
    }

    fun getTopWallpapers(categoryName: String? = null) {
        val selectedCategory = categoryName ?: uiState.value.filters.find { it.isSelected }?.name
        
        handleApiCall(
            apiCall = { 
                if (selectedCategory == null || selectedCategory == "All") {
                    getTopWallpapersUseCase()
                } else {
                    getWallpapersByCategoryUseCase(selectedCategory)
                }
            },
            onSuccess = { dtoList ->
                val topWallpapers = dtoList.map { it.toPresentation() }
                setState { copy(topWallpaperUiState = UiState.Success(topWallpapers)) }
            },
            onError = { throwable ->
                setState { copy(topWallpaperUiState = UiState.Error(throwable)) }
            },
        )
    }

    private fun getCategories() {
        handleApiCall(
            apiCall = { getCategoriesUseCase() },
            onSuccess = { dtoList ->
                val categories = dtoList.map { it.toPresentation() }
                
                // Also create filters for Home tab
                val filters = mutableListOf(FilterUIModel("All", true))
                filters.addAll(categories.map { FilterUIModel(it.title, false) })
                
                setState { 
                    copy(
                        categoriesUiState = UiState.Success(categories),
                        filters = filters
                    ) 
                }
            },
            onError = { throwable ->
                setState { copy(categoriesUiState = UiState.Error(throwable)) }
            },
        )
    }

    fun selectFilter(filter: FilterUIModel) {
        val newFilters = uiState.value.filters.map {
            it.copy(isSelected = it.name == filter.name)
        }
        setState { copy(filters = newFilters) }
        getTopWallpapers(filter.name)
    }

    fun setSelectedTab(index: Int) {
        setState { copy(selectedTab = index) }
    }

    fun setWallpaper(uri: android.net.Uri, flag: Int) {
        setState { copy(isSettingWallpaper = true) }
        handleApiCall(
            apiCall = { setWallpaperUseCase(uri, flag) },
            onSuccess = { 
                setState { copy(isSettingWallpaper = false) }
            },
            onError = { 
                setState { copy(isSettingWallpaper = false) }
            }
        )
    }
}

data class HomeUiState(
    val featuredUiState: UiState<List<WallpaperUIModel>> = UiState.None,
    val topWallpaperUiState: UiState<List<WallpaperUIModel>> = UiState.None,
    val categoriesUiState: UiState<List<CategoryUIModel>> = UiState.None,
    val filters: List<FilterUIModel> = emptyList(),
    val selectedTab: Int = 0,
    val isSettingWallpaper: Boolean = false
)
