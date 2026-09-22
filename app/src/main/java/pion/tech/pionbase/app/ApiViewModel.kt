package pion.tech.pionbase.app

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.data.model.appCategory.toPresentation
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.data.model.template.toPresentation
import pion.tech.pionbase.domain.usecase.api.GetAppCategoryUseCase
import pion.tech.pionbase.domain.usecase.api.GetTemplateDataUseCase
import pion.tech.pionbase.util.UiState
import pion.tech.pionbase.util.handleApiCall

class ApiViewModel(
    private val getAppCategoryUseCase: GetAppCategoryUseCase,
    private val getTemplateDataUseCase: GetTemplateDataUseCase,
) : BaseViewModel<ApiUiState, Nothing>(ApiUiState()) {

    fun getAppId() {
        setState { copy(categoryUiState = UiState.Loading) }

        handleApiCall(
            apiCall = { getAppCategoryUseCase() },
            onSuccess = { data ->
                val categories = data.map { item -> item.toPresentation() }
                setState { copy(categoryUiState = UiState.Success(categories)) }
            },
            onError = { throwable ->
                setState { copy(categoryUiState = UiState.Error(throwable)) }
            },
        )
    }

    fun getTemplate(categoryId: String) {
        setState { copy(templateUiState = UiState.Loading) }

        handleApiCall(
            apiCall = { getTemplateDataUseCase(categoryId) },
            onSuccess = { data ->
                val templates = data.map { item -> item.toPresentation() }
                setState { copy(templateUiState = UiState.Success(templates)) }
            },
            onError = { throwable ->
                setState { copy(templateUiState = UiState.Error(throwable)) }
            },
        )
    }

    fun loadTemplateFromTemplateCategoryName(name: String = "Template") {
        val categoryUiState = uiState.value.categoryUiState
        if (categoryUiState is UiState.Success) {
            val templateCategoryId = categoryUiState.data.firstOrNull { item -> item.name == name }?.id
            if (templateCategoryId != null) {
                getTemplate(templateCategoryId)
            }
        }
    }

    init {
        getAppId()
    }
}

data class ApiUiState(
    val categoryUiState: UiState<List<AppCategoryUIModel>> = UiState.None,
    val templateUiState: UiState<List<TemplateUIModel>> = UiState.None,
)
