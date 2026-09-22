package pion.tech.pionbase.app

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.domain.usecase.common.GetIsPremiumUseCase
import pion.tech.pionbase.domain.usecase.common.SetIsPremiumUseCase
import pion.tech.pionbase.util.handleApiCall

class CommonViewModel(
    private val getIsPremiumUseCase: GetIsPremiumUseCase,
    private val setIsPremiumUseCase: SetIsPremiumUseCase,
) : BaseViewModel<CommonUiState, Nothing>(CommonUiState()) {

    private fun getIsPremium() {
        handleApiCall(
            apiCall = { getIsPremiumUseCase() },
            onSuccess = { isPremium ->
                setState { copy(isPremium = isPremium) }
            },
        )
    }

    fun setPremium(isPremium: Boolean) {
        handleApiCall(apiCall = { setIsPremiumUseCase(isPremium) })
    }

    init {
        getIsPremium()
    }
}

data class CommonUiState(
    val isPremium: Boolean = false,
)
