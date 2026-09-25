package pion.tech.pionbase.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pion.tech.pionbase.domain.usecase.api.GetAppCategoryUseCase
import pion.tech.pionbase.domain.usecase.api.GetTemplateDataUseCase
import pion.tech.pionbase.domain.usecase.common.GetIsPremiumUseCase
import pion.tech.pionbase.domain.usecase.common.SetIsPremiumUseCase
import pion.tech.pionbase.domain.usecase.home.DownloadImageToBitmapUseCase
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
import pion.tech.pionbase.domain.usecase.language.GetIsFirstLaunchUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguageUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.domain.usecase.language.SetIsFirstLaunchUseCase
import pion.tech.pionbase.domain.usecase.language.SetLanguageUseCase
import pion.tech.pionbase.domain.usecase.quote.GetQuotesUseCase
import pion.tech.pionbase.domain.usecase.quote.SaveQuoteWallpaperUseCase
import pion.tech.pionbase.domain.usecase.settings.ClearCacheUseCase
import pion.tech.pionbase.domain.usecase.settings.FormatCacheSizeUseCase
import pion.tech.pionbase.domain.usecase.settings.GetAutoWallpaperSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.GetBatterySaverSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.GetCacheSizeUseCase
import pion.tech.pionbase.domain.usecase.settings.GetThemeSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetAutoWallpaperSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetBatterySaverSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetThemeSettingsUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.DownloadWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFavoriteWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFeaturedWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetTopWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetWallpapersByCategoryUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.IsFavoriteWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SearchWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.SetRandomNextWallpaperUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.ToggleFavoriteUseCase

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
    factoryOf(::GetThemeSettingsUseCase)
    factoryOf(::SetThemeSettingsUseCase)
    factoryOf(::GetBatterySaverSettingsUseCase)
    factoryOf(::SetBatterySaverSettingsUseCase)
    factoryOf(::GetWallpapersByCategoryUseCase)
    factoryOf(::IsFavoriteWallpaperUseCase)
    factoryOf(::SearchWallpapersUseCase)
    factoryOf(::DownloadWallpaperUseCase)
    factoryOf(::GetCacheSizeUseCase)
    factoryOf(::ClearCacheUseCase)
    factoryOf(::FormatCacheSizeUseCase)
    factoryOf(::SetRandomNextWallpaperUseCase)
}

val languageUseCaseModule = module {
    factoryOf(::GetLanguagesUseCase)
    factoryOf(::GetLanguageUseCase)
    factoryOf(::SetLanguageUseCase)
    factoryOf(::GetIsFirstLaunchUseCase)
    factoryOf(::SetIsFirstLaunchUseCase)
}

val apiUseCaseModule = module {
    factoryOf(::GetAppCategoryUseCase)
    factoryOf(::GetTemplateDataUseCase)
}

val commonUseCaseModule = module {
    factoryOf(::GetIsPremiumUseCase)
    factoryOf(::SetIsPremiumUseCase)
}

val quoteUseCaseModule = module {
    factoryOf(::GetQuotesUseCase)
    factoryOf(::SaveQuoteWallpaperUseCase)
}

val useCaseModule = module {
    includes(
        homeUseCaseModule,
        wallpaperUseCaseModule,
        languageUseCaseModule,
        apiUseCaseModule,
        commonUseCaseModule,
        quoteUseCaseModule,
    )
}
