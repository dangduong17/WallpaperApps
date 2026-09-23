package pion.tech.pionbase.data.model.quote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuoteUIModel(
    val id: Int = 0,
    val quote: String = "",
    val author: String = "",
    val category: String = ""
) : Parcelable
