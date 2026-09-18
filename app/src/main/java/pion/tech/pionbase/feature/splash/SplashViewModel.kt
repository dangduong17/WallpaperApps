package pion.tech.pionbase.feature.splash

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.handleApiCall

class SplashViewModel(
    private val dataStoreRepository: DataStoreRepository
) : BaseViewModel<SplashUiState, Nothing>(SplashUiState()) {

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        handleApiCall(
            apiCall = { dataStoreRepository.getIsFirstLaunch() },
            onSuccess = { isFirstLaunch ->
                setState { copy(isFirstLaunch = isFirstLaunch) }
            }
        )
    }
}

data class SplashUiState(
    val isFirstLaunch: Boolean? = null
)
