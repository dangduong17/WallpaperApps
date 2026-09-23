package pion.tech.pionbase.feature.quoteEditor

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.text.Layout
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.data.model.quote.QuoteUIModel
import pion.tech.pionbase.domain.usecase.quote.GetQuotesUseCase
import pion.tech.pionbase.domain.usecase.quote.SaveQuoteWallpaperUseCase
import pion.tech.pionbase.util.handleApiCall

data class QuoteEditorUiState(
    val quotes: List<QuoteUIModel> = emptyList(),
    val selectedCategory: String = "All",
    val currentQuoteText: String = "The secret of getting ahead is getting started.",
    val selectedFontResId: Int? = R.font.font_500,
    val selectedColor: Int = Color.WHITE,
    val textSizeSp: Float = 28f,
    val alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER,
    val shadowRadius: Float = 6f,
    val shadowDx: Float = 3f,
    val shadowDy: Float = 3f,
    val shadowColor: Int = Color.BLACK,
    val isLoading: Boolean = false
)

sealed class QuoteEditorEvent {
    data class SaveSuccess(val uri: Uri) : QuoteEditorEvent()
    data class SaveError(val throwable: Throwable) : QuoteEditorEvent()
    object SetWallpaperSuccess : QuoteEditorEvent()
    data class SetWallpaperError(val throwable: Throwable) : QuoteEditorEvent()
}

class QuoteEditorViewModel(
    private val getQuotesUseCase: GetQuotesUseCase,
    private val saveQuoteWallpaperUseCase: SaveQuoteWallpaperUseCase,
    private val setWallpaperUseCase: pion.tech.pionbase.domain.usecase.home.SetWallpaperUseCase
) : BaseViewModel<QuoteEditorUiState, QuoteEditorEvent>(QuoteEditorUiState()) {

    init {
        loadQuotes("All")
    }

    fun loadQuotes(category: String) {
        handleApiCall(
            apiCall = { getQuotesUseCase(category) },
            onSuccess = { dtos ->
                val uiModels = dtos.map { it.toPresentation() }
                val defaultQuote = uiModels.firstOrNull()?.quote ?: getCurrentState().currentQuoteText
                setState {
                    copy(
                        quotes = uiModels,
                        selectedCategory = category,
                        currentQuoteText = if (currentQuoteText == "The secret of getting ahead is getting started." && defaultQuote.isNotEmpty()) defaultQuote else currentQuoteText
                    )
                }
            },
            onError = { _ -> }
        )
    }

    fun updateQuoteText(text: String) {
        setState { copy(currentQuoteText = text) }
    }

    fun updateFontResId(resId: Int?) {
        setState { copy(selectedFontResId = resId) }
    }

    fun updateColor(color: Int) {
        setState { copy(selectedColor = color) }
    }

    fun updateTextSize(sizeSp: Float) {
        setState { copy(textSizeSp = sizeSp) }
    }

    fun updateAlignment(alignment: Layout.Alignment) {
        setState { copy(alignment = alignment) }
    }

    fun saveWallpaper(bitmap: Bitmap) {
        setState { copy(isLoading = true) }
        handleApiCall(
            apiCall = { saveQuoteWallpaperUseCase(bitmap) },
            onSuccess = { uri ->
                setState { copy(isLoading = false) }
                launchMain {
                    setEvent(QuoteEditorEvent.SaveSuccess(uri))
                }
            },
            onError = { throwable ->
                setState { copy(isLoading = false) }
                launchMain {
                    setEvent(QuoteEditorEvent.SaveError(throwable))
                }
            }
        )
    }

    fun applyWallpaper(bitmap: Bitmap, flag: Int) {
        setState { copy(isLoading = true) }
        handleApiCall(
            apiCall = { setWallpaperUseCase(bitmap, flag) },
            onSuccess = {
                setState { copy(isLoading = false) }
                launchMain {
                    setEvent(QuoteEditorEvent.SetWallpaperSuccess)
                }
            },
            onError = { throwable ->
                setState { copy(isLoading = false) }
                launchMain {
                    setEvent(QuoteEditorEvent.SetWallpaperError(throwable))
                }
            }
        )
    }
}
