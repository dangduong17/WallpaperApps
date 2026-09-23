package pion.tech.pionbase.data.repository.quoteRepository

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.model.quote.QuoteDtoModel
import pion.tech.pionbase.util.Result

interface QuoteRepository {
    fun getQuotes(): Flow<Result<List<QuoteDtoModel>>>
    fun getQuotesByCategory(category: String): Flow<Result<List<QuoteDtoModel>>>
}
