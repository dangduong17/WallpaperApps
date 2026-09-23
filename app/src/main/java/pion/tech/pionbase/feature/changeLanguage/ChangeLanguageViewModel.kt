package pion.tech.pionbase.feature.changeLanguage

import android.content.Context
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.firstOrNull
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.domain.usecase.wallpaper.GetCategoriesUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetFeaturedWallpapersUseCase
import pion.tech.pionbase.domain.usecase.wallpaper.GetTopWallpapersUseCase
import pion.tech.pionbase.util.Result
import timber.log.Timber

class ChangeLanguageViewModel(
    private val getFeaturedWallpapersUseCase: GetFeaturedWallpapersUseCase,
    private val getTopWallpapersUseCase: GetTopWallpapersUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val context: Context
) : BaseViewModel<Unit, Nothing>(Unit) {

    fun preloadDataAndImages() {
        launchIO {
            try {
                // 1. Pre-fetch API & save to Room DB
                getCategoriesUseCase().firstOrNull()
                val featuredResult = getFeaturedWallpapersUseCase().firstOrNull()
                val topResult = getTopWallpapersUseCase().firstOrNull()

                // 2. Pre-download top thumbnail images into Glide disk cache
                if (featuredResult is Result.Success) {
                    featuredResult.data.take(10).forEach { dto ->
                        try {
                            Glide.with(context)
                                .downloadOnly()
                                .load(dto.resolvedThumbnailUrl)
                                .submit()
                        } catch (_: Exception) {}
                    }
                }
                if (topResult is Result.Success) {
                    topResult.data.take(15).forEach { dto ->
                        try {
                            Glide.with(context)
                                .downloadOnly()
                                .load(dto.resolvedThumbnailUrl)
                                .submit()
                        } catch (_: Exception) {}
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error preloading data and images in ChangeLanguageViewModel")
            }
        }
    }
}
