package pion.tech.pionbase.domain.usecase.api

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.appCategory.AppCategoryDtoModel
import pion.tech.pionbase.data.repository.apiRepository.ApiRepository
import pion.tech.pionbase.util.Result

class GetAppCategoryUseCase(
    private val apiRepository: ApiRepository,
) {
    operator fun invoke(): Flow<Result<List<AppCategoryDtoModel>>> =
        apiRepository.getAppCategory()
}
