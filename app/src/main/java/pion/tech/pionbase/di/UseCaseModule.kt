package pion.tech.pionbase.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pion.tech.pionbase.domain.usecase.home.DownloadImageToBitmapUseCase
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.DownloadWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFavoriteWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFeaturedWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetTopWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetWallpapersByCategoryUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.IsFavoriteWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SearchWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.ToggleFavoriteUseCase
import pion.tech.pionbase.domain.usecase.settings.GetAutoWallpaperSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetAutoWallpaperSettingsUseCase

val homeUseCaseModule = module {
    factoryOf(::GetInstalledAppsUseCase)
    factoryOf(::SetWallpaperUseCase)
    factoryOf(::DownloadImageToBitmapUseCase)
}

val wallpaperUseCaseModule = module {
    factoryOf(::GetFeaturedWallpapersUseCase)
    factoryOf(::GetTopWallpapersUseCase)
    factoryOf(::GetCategoriesUseCase)
    factoryOf(::GetFavoriteWallpapersUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::GetAutoWallpaperSettingsUseCase)
    factoryOf(::SetAutoWallpaperSettingsUseCase)
    factoryOf(::GetWallpapersByCategoryUseCase)
    factoryOf(::IsFavoriteWallpaperUseCase)
    factoryOf(::SearchWallpapersUseCase)
    factoryOf(::DownloadWallpaperUseCase)
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
