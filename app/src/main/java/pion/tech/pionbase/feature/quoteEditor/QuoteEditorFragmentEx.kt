package pion.tech.pionbase.feature.quoteEditor

import android.app.AlertDialog
import android.app.WallpaperManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.text.InputType
import android.text.Layout
import android.view.View
import android.widget.EditText
import androidx.core.graphics.drawable.toBitmap
import pion.tech.pionbase.R
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.data.model.quote.QuoteUIModel
import pion.tech.pionbase.feature.quoteEditor.adapter.ColorAdapter
import pion.tech.pionbase.feature.quoteEditor.adapter.FontAdapter
import pion.tech.pionbase.feature.quoteEditor.adapter.FontItem
import pion.tech.pionbase.feature.quoteEditor.bottomSheet.QuoteBottomSheet
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.showSuccessSnackbar
import pion.tech.pionbase.util.showErrorSnackbar
import pion.tech.pionbase.util.safeShowBottomSheet
import pion.tech.pionbase.util.setPreventDoubleClick

fun QuoteEditorFragment.initView() {
    val fonts = listOf(
        FontItem("Light", R.font.font_100),
        FontItem("Thin", R.font.font_200),
        FontItem("Regular", R.font.font_400),
        FontItem("Medium", R.font.font_500),
        FontItem("SemiBold", R.font.font_600),
        FontItem("Bold", R.font.font_700),
        FontItem("ExtraBold", R.font.font_800),
        FontItem("Black", R.font.font_900)
    )

    val fontAdapter = FontAdapter()
    fontAdapter.setListener(object : FontAdapter.Listener {
        override fun onSelectFont(fontItem: FontItem) {
            viewModel.updateFontResId(fontItem.fontResId)
        }
    })
    binding.rvFonts.adapter = fontAdapter
    fontAdapter.submitList(fonts)

    val colors = listOf(
        Color.WHITE,
        Color.BLACK,
        Color.YELLOW,
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.CYAN,
        Color.MAGENTA,
        Color.parseColor("#FF9800"),
        Color.parseColor("#E91E63"),
        Color.parseColor("#9C27B0"),
        Color.parseColor("#00BCD4")
    )

    val colorAdapter = ColorAdapter()
    colorAdapter.setListener(object : ColorAdapter.Listener {
        override fun onSelectColor(color: Int) {
            viewModel.updateColor(color)
        }
    })
    binding.rvColors.adapter = colorAdapter
    colorAdapter.submitList(colors)

    setupTabNavigation()
}

fun QuoteEditorFragment.setupTabNavigation() {
    binding.tvTabQuote.setPreventDoubleClick {
        openQuoteBottomSheet()
        highlightTab(0)
    }

    binding.tvTabFont.setPreventDoubleClick {
        binding.rvFonts.visibility = View.VISIBLE
        binding.rvColors.visibility = View.GONE
        binding.llSizeControl.visibility = View.GONE
        binding.llAlignControl.visibility = View.GONE
        highlightTab(1)
    }

    binding.tvTabSize.setPreventDoubleClick {
        binding.rvFonts.visibility = View.GONE
        binding.rvColors.visibility = View.GONE
        binding.llSizeControl.visibility = View.VISIBLE
        binding.llAlignControl.visibility = View.GONE
        highlightTab(2)
    }

    binding.tvTabColor.setPreventDoubleClick {
        binding.rvFonts.visibility = View.GONE
        binding.rvColors.visibility = View.VISIBLE
        binding.llSizeControl.visibility = View.GONE
        binding.llAlignControl.visibility = View.GONE
        highlightTab(3)
    }

    binding.tvTabAlign.setPreventDoubleClick {
        binding.rvFonts.visibility = View.GONE
        binding.rvColors.visibility = View.GONE
        binding.llSizeControl.visibility = View.GONE
        binding.llAlignControl.visibility = View.VISIBLE
        highlightTab(4)
    }
}

fun QuoteEditorFragment.highlightTab(index: Int) {
    val tabs = listOf(
        binding.tvTabQuote,
        binding.tvTabFont,
        binding.tvTabSize,
        binding.tvTabColor,
        binding.tvTabAlign
    )
    tabs.forEachIndexed { i, textView ->
        if (i == index) {
            textView.setTextColor(requireContext().getColor(R.color.white))
            textView.textSize = 13f
        } else {
            textView.setTextColor(Color.parseColor("#888888"))
            textView.textSize = 12f
        }
    }
}

fun QuoteEditorFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClick {
        if (isResumed && navigator.getCurrentDestinationId() == R.id.quoteEditorFragment) {
            navigator.navigateUp()
        }
    }

    binding.sliderTextSize.addOnChangeListener { _, value, _ ->
        viewModel.updateTextSize(value)
    }

    binding.btnAlignLeft.setPreventDoubleClick {
        viewModel.updateAlignment(Layout.Alignment.ALIGN_NORMAL)
    }

    binding.btnAlignCenter.setPreventDoubleClick {
        viewModel.updateAlignment(Layout.Alignment.ALIGN_CENTER)
    }

    binding.btnAlignRight.setPreventDoubleClick {
        viewModel.updateAlignment(Layout.Alignment.ALIGN_OPPOSITE)
    }

    binding.stickerTextView.setPreventDoubleClick {
        openEditTextDialog()
    }

    binding.btnSave.setPreventDoubleClick {
        val bgDrawable = binding.ivBackgroundWallpaper.drawable
        if (bgDrawable is BitmapDrawable) {
            val bgBitmap = bgDrawable.bitmap
            val renderedBitmap = binding.stickerTextView.renderToBitmap(bgBitmap)
            viewModel.saveWallpaper(renderedBitmap)
        } else {
            val bgBitmap = bgDrawable.toBitmap()
            val renderedBitmap = binding.stickerTextView.renderToBitmap(bgBitmap)
            viewModel.saveWallpaper(renderedBitmap)
        }
    }

    binding.btnApplyWallpaper.setPreventDoubleClick {
        applyWallpaperDirectly()
    }
}

fun QuoteEditorFragment.openEditTextDialog() {
    val context = requireContext()
    val editText = EditText(context).apply {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        setText(viewModel.uiState.value.currentQuoteText)
    }

    AlertDialog.Builder(context)
        .setTitle(getString(R.string.edit_text))
        .setView(editText)
        .setPositiveButton(getString(R.string.ok)) { _, _ ->
            val newText = editText.text.toString().trim()
            if (newText.isNotEmpty()) {
                viewModel.updateQuoteText(newText)
            }
        }
        .setNegativeButton(getString(R.string.cancel), null)
        .show()
}

fun QuoteEditorFragment.openQuoteBottomSheet() {
    val state = viewModel.uiState.value
    val dynamicCategories = state.quotes.map { it.category }.distinct().filter { it.isNotEmpty() }
    val categories = listOf("All") + dynamicCategories
    
    val bottomSheet = QuoteBottomSheet.newInstance().apply {
        setData(state.quotes, categories)
        setListener(object : QuoteBottomSheet.Listener {
            override fun onQuoteSelected(quote: QuoteUIModel) {
                viewModel.updateQuoteText(quote.quote)
            }

            override fun onCategorySelected(category: String) {
                viewModel.loadQuotes(category)
            }
        })
    }
    safeShowBottomSheet(bottomSheet)
}

fun QuoteEditorFragment.applyWallpaperDirectly() {
    val context = requireContext()
    val bgDrawable = binding.ivBackgroundWallpaper.drawable ?: return
    val bgBitmap = (bgDrawable as? BitmapDrawable)?.bitmap ?: bgDrawable.toBitmap()
    val renderedBitmap = binding.stickerTextView.renderToBitmap(bgBitmap)

    val options = arrayOf(
        getString(R.string.home_screen),
        getString(R.string.lock_screen),
        getString(R.string.both)
    )

    AlertDialog.Builder(context)
        .setTitle(getString(R.string.set_as_wallpaper))
        .setItems(options) { _, which ->
            val flag = when (which) {
                0 -> WallpaperManager.FLAG_SYSTEM
                1 -> WallpaperManager.FLAG_LOCK
                else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            }

            viewModel.applyWallpaper(renderedBitmap, flag)
        }
        .show()
}
