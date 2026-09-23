package pion.tech.pionbase.data.model.quote

import com.google.gson.annotations.SerializedName

data class QuoteDtoModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("quote")
    val quote: String,
    @SerializedName("author")
    val author: String?,
    @SerializedName("category")
    val category: String?
) {
    fun toPresentation(): QuoteUIModel {
        return QuoteUIModel(
            id = id,
            quote = quote,
            author = author.orEmpty(),
            category = category.orEmpty()
        )
    }
}
