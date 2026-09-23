package pion.tech.pionbase.feature.splash

import android.content.Context
import com.bumptech.glide.Glide
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.domain.usecase.language.GetIsFirstLaunchUseCase
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.handleApiCall
import timber.log.Timber

class SplashViewModel(
    private val getIsFirstLaunchUseCase: GetIsFirstLaunchUseCase,
    private val getLanguagesUseCase: GetLanguagesUseCase,
    private val context: Context
) : BaseViewModel<SplashUiState, Nothing>(SplashUiState()) {

    init {
        checkFirstLaunch()
        preloadLanguageFlags()
    }

    private fun preloadLanguageFlags() {
        launchIO {
            try {
                getLanguagesUseCase().collect { result ->
                    if (result is Result.Success) {
                        result.data.forEach { dto ->
                            try {
                                Glide.with(context)
                                    .downloadOnly()
                                    .load(dto.thumbnail)
                                    .submit()
                            } catch (_: Exception) {}
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error preloading language flags")
            }
        }
    }

    private fun checkFirstLaunch() {
        handleApiCall(
            apiCall = { getIsFirstLaunchUseCase() },
            onSuccess = { isFirstLaunch ->
                setState { copy(isFirstLaunch = isFirstLaunch) }
            }
        )
    }
}

data class SplashUiState(
    val isFirstLaunch: Boolean? = null
)
