package pion.tech.pionbase.feature.quoteEditor

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentQuoteEditorBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.loadThumbnailAndFull

class QuoteEditorFragment : BaseFragment<FragmentQuoteEditorBinding, QuoteEditorViewModel>(
    FragmentQuoteEditorBinding::inflate,
    QuoteEditorViewModel::class
) {
    val args: QuoteEditorFragmentArgs by navArgs()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
        binding.ivBackgroundWallpaper.loadThumbnailAndFull(args.wallpaper.resolvedThumbnailUrl, args.wallpaper.imageUrl)
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.currentQuoteText }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { quoteText ->
                binding.stickerTextView.text = quoteText
            }

        viewModel.uiState
            .map { it.selectedFontResId }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { fontResId ->
                if (fontResId != null) {
                    binding.stickerTextView.textTypeface = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), fontResId)
                        ?: android.graphics.Typeface.DEFAULT
                }
            }

        viewModel.uiState
            .map { it.selectedColor }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { color ->
                binding.stickerTextView.textColor = color
            }

        viewModel.uiState
            .map { it.textSizeSp }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { sizeSp ->
                binding.stickerTextView.textSizeSp = sizeSp
            }

        viewModel.uiState
            .map { it.alignment }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { align ->
                binding.stickerTextView.textAlignment = align
            }

        viewModel.uiState
            .map { it.isLoading }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isLoading ->
                showHideLoading(isLoading)
            }

        viewModel.uiEvent
            .collectFlowOnView(viewLifecycleOwner) { event ->
                when (event) {
                    is QuoteEditorEvent.SaveSuccess -> {
                        displayToast(getString(R.string.save_quote_success))
                    }
                    is QuoteEditorEvent.SaveError -> {
                        displayToast(getString(R.string.error, event.throwable.message))
                    }
                    is QuoteEditorEvent.SetWallpaperSuccess -> {
                        displayToast(getString(R.string.set_wallpaper_success))
                    }
                    is QuoteEditorEvent.SetWallpaperError -> {
                        displayToast(getString(R.string.error, event.throwable.message))
                    }
                }
            }
    }
}
