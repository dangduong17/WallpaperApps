package pion.tech.pionbase.data.repository.quoteRepository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pion.tech.pionbase.data.model.quote.QuoteDtoModel
import pion.tech.pionbase.util.Result

class QuoteRepositoryImpl(
    private val context: Context,
    private val gson: Gson,
) : QuoteRepository {

    override fun getQuotes(): Flow<Result<List<QuoteDtoModel>>> = flow<Result<List<QuoteDtoModel>>> {
        val quotes = loadQuotesFromAssets()
        emit(Result.Success(quotes))
    }.catch {
        emit(Result.Error(it))
    }.flowOn(Dispatchers.IO)

    override fun getQuotesByCategory(category: String): Flow<Result<List<QuoteDtoModel>>> = flow<Result<List<QuoteDtoModel>>> {
        val allQuotes = loadQuotesFromAssets()
        val filtered = if (category.isEmpty() || category.equals("All", ignoreCase = true)) {
            allQuotes
        } else {
            allQuotes.filter { it.category.equals(category, ignoreCase = true) }
        }
        emit(Result.Success(filtered))
    }.catch {
        emit(Result.Error(it))
    }.flowOn(Dispatchers.IO)

    private fun loadQuotesFromAssets(): List<QuoteDtoModel> {
        return try {
            val jsonString = context.assets.open("quotes.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<QuoteDtoModel>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
