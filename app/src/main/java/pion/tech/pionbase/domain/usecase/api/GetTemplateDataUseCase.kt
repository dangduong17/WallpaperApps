package pion.tech.pionbase.domain.usecase.api

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.template.TemplateDtoModel
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.util.Result

class GetTemplateDataUseCase(
    private val apiRepository: ApiRepository,
) {
    operator fun invoke(categoryId: String): Flow<Result<List<TemplateDtoModel>>> =
        apiRepository.getTemplateData(categoryId)
}
