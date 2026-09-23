package pion.tech.pionbase.domain.usecase.quote

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.quote.QuoteDtoModel
import pion.tech.pionbase.data.repository.quoteRepository.QuoteRepository
import pion.tech.pionbase.util.Result

class GetQuotesUseCase(
    private val quoteRepository: QuoteRepository
) {
    operator fun invoke(category: String = "All"): Flow<Result<List<QuoteDtoModel>>> {
        return quoteRepository.getQuotesByCategory(category)
    }
}
