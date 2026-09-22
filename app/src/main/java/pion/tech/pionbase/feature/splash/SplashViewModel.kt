package pion.tech.pionbase.feature.splash

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.domain.usecase.language.GetIsFirstLaunchUseCase
import pion.tech.pionbase.util.handleApiCall

class SplashViewModel(
    private val getIsFirstLaunchUseCase: GetIsFirstLaunchUseCase
) : BaseViewModel<SplashUiState, Nothing>(SplashUiState()) {

    init {
        checkFirstLaunch()
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
