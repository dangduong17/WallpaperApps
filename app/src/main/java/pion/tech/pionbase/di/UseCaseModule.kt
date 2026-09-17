package pion.tech.pionbase.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pion.tech.pionbase.domain.usecase.home.GetInstalledAppsUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFeaturedWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetTopWallpapersUseCase

val homeUseCaseModule = module {
    factoryOf(::GetInstalledAppsUseCase)
}

val wallpaperUseCaseModule = module {
    factoryOf(::GetFeaturedWallpapersUseCase)
    factoryOf(::GetTopWallpapersUseCase)
    factoryOf(::GetCategoriesUseCase)
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
