package pion.tech.pionbase.feature.language

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.data.model.language.toPresentation
import pion.tech.pionbase.domain.usecase.language.GetLanguagesUseCase
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class LanguageViewModel(
    private val getLanguagesUseCase: GetLanguagesUseCase,
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel<LanguageUiState, Nothing>(LanguageUiState()) {

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        setState { copy(languagesUiState = UiState.Loading) }
        handleApiCall(
            apiCall = { getLanguagesUseCase() },
            onSuccess = { dtoList ->
                val languages = dtoList.map { it.toPresentation() }
                // Load saved language or default to "en"
                handleApiCall(
                    apiCall = { dataStoreRepository.getLanguage() },
                    onSuccess = { savedLocale ->
                        val selectedLanguage = languages.find { it.localeCode == savedLocale } ?: languages.find { it.localeCode == "en" }
                        setState {
                            copy(
                                languagesUiState = UiState.Success(languages),
                                selectedLanguage = selectedLanguage ?: languages.firstOrNull()
                            )
                        }
                    },
                    onError = {
                        val defaultLanguage = languages.find { it.localeCode == "en" }
                        setState {
                            copy(
                                languagesUiState = UiState.Success(languages),
                                selectedLanguage = defaultLanguage ?: languages.firstOrNull()
                            )
                        }
                    }
                )
            },
            onError = { throwable ->
                setState {
                    copy(
                        languagesUiState = UiState.Error(throwable)
                    )
                }
            },
        )
    }

    fun selectLanguage(item: LanguageUIModel) {
        setState {
            copy(
                selectedLanguage = item,
            )
        }
    }

    fun applySelectedLanguage() {
        val selected = uiState.value.selectedLanguage ?: return
        handleApiCall(apiCall = { dataStoreRepository.setLanguage(selected.localeCode) })
    }

    fun setFirstLaunchFalse() {
        handleApiCall(apiCall = { dataStoreRepository.setIsFirstLaunch(false) })
    }

    fun getSelectedLanguage(): LanguageUIModel? = uiState.value.selectedLanguage
}

data class LanguageUiState(
    val languagesUiState: UiState<List<LanguageUIModel>> = UiState.None,
    val selectedLanguage: LanguageUIModel? = null,
)

fun LanguageUiState.getSelectedLanguageListUiState(): UiState<List<LanguageUIModel>> {
    return when (val state = languagesUiState) {
        is UiState.Success -> {
            val targetLocaleCode = selectedLanguage?.localeCode
            UiState.Success(state.data.map { item ->
                val shouldBeSelected = (item.localeCode == targetLocaleCode)
                if (item.isSelected == shouldBeSelected) {
                    item
                } else {
                    item.copy(isSelected = shouldBeSelected)
                }
            })
        }
        else -> state
    }
}
