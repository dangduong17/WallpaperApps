package pion.tech.pionbase.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pion.tech.pionbase.app.ApiViewModel
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.feature.home.HomeViewModel
import pion.tech.pionbase.feature.language.LanguageViewModel
import pion.tech.pionbase.feature.onboard.OnboardViewModel
import pion.tech.pionbase.feature.setting.SettingViewModel
import pion.tech.pionbase.feature.splash.SplashViewModel
import pion.tech.pionbase.feature.wallpaperDetail.WallpaperDetailViewModel
import pion.tech.pionbase.feature.favorite.FavoriteViewModel
import pion.tech.pionbase.feature.search.SearchViewModel
import pion.tech.pionbase.feature.categoryDetail.CategoryDetailViewModel
import pion.tech.pionbase.feature.urlWallpaper.UrlWallpaperViewModel

val viewModelModule =
    module {
        viewModelOf(::ApiViewModel)
        viewModelOf(::CommonViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::LanguageViewModel)
        viewModelOf(::SplashViewModel)
        viewModelOf(::SettingViewModel)
        viewModelOf(::OnboardViewModel)
        viewModelOf(::WallpaperDetailViewModel)
        viewModelOf(::FavoriteViewModel)
        viewModelOf(::SearchViewModel)
        viewModelOf(::CategoryDetailViewModel)
        viewModelOf(::UrlWallpaperViewModel)
    }
