package pion.tech.pionbase.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFavoriteWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFeaturedWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetTopWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetWallpapersByCategoryUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.IsFavoriteWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SearchWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.ToggleFavoriteUseCase

val homeUseCaseModule = module {
    factoryOf(::GetInstalledAppsUseCase)
}

val wallpaperUseCaseModule = module {
    factoryOf(::GetFeaturedWallpapersUseCase)
    factoryOf(::GetTopWallpapersUseCase)
    factoryOf(::GetCategoriesUseCase)
    factoryOf(::GetFavoriteWallpapersUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::GetWallpapersByCategoryUseCase)
    factoryOf(::IsFavoriteWallpaperUseCase)
    factoryOf(::SearchWallpapersUseCase)
}

val languageUseCaseModule = module {
    factoryOf(::GetLanguagesUseCase)
}

val useCaseModule = module {
    includes(
        homeUseCaseModule,
        wallpaperUseCaseModule,
        languageUseCaseModule
    )
}
