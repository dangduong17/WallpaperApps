package pion.tech.pionbase.feature.quoteEditor.bottomSheet

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import pion.tech.pionbase.base.BaseBottomSheetDialogFragment
import pion.tech.pionbase.data.model.quote.QuoteUIModel
import pion.tech.pionbase.databinding.BottomSheetQuoteSelectorBinding
import pion.tech.pionbase.feature.quoteEditor.adapter.QuoteAdapter

class QuoteBottomSheet : BaseBottomSheetDialogFragment<BottomSheetQuoteSelectorBinding>(
    BottomSheetQuoteSelectorBinding::inflate
) {
    interface Listener {
        fun onQuoteSelected(quote: QuoteUIModel)
        fun onCategorySelected(category: String)
    }

    private var listener: Listener? = null
    private var quotes: List<QuoteUIModel> = emptyList()
    private var categories: List<String> = emptyList()
    private val quoteAdapter = QuoteAdapter()

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setData(quotes: List<QuoteUIModel>, categories: List<String>) {
        this.quotes = quotes
        this.categories = categories
        if (isAdded) {
            updateUI()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        quoteAdapter.setListener(object : QuoteAdapter.Listener {
            override fun onSelectQuote(quote: QuoteUIModel) {
                listener?.onQuoteSelected(quote)
                dismiss()
            }
        })
        binding.rvQuotes.adapter = quoteAdapter
        updateUI()

        binding.tabCategories.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.text?.toString()?.let { category ->
                    listener?.onCategorySelected(category)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateUI() {
        quoteAdapter.submitList(quotes)
        binding.tabCategories.removeAllTabs()
        categories.forEach { category ->
            binding.tabCategories.addTab(binding.tabCategories.newTab().setText(category))
        }
    }

    fun updateQuotes(newQuotes: List<QuoteUIModel>) {
        this.quotes = newQuotes
        quoteAdapter.submitList(newQuotes)
    }

    companion object {
        fun newInstance(): QuoteBottomSheet = QuoteBottomSheet()
    }
}
