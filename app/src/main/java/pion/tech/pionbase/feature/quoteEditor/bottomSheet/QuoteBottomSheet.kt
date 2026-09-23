package pion.tech.pionbase.feature.quoteEditor.bottomSheet

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import pion.tech.pionbase.base.BaseBottomSheetDialogFragment
import pion.tech.pionbase.data.model.quote.QuoteUIModel
import pion.tech.pionbase.databinding.BottomSheetQuoteSelectorBinding
import pion.tech.pionbase.feature.quoteEditor.adapter.QuoteAdapter

class QuoteBottomSheet(
    private val quotes: List<QuoteUIModel>,
    private val categories: List<String>,
    private val onQuoteSelected: (QuoteUIModel) -> Unit,
    private val onCategorySelected: (String) -> Unit
) : BaseBottomSheetDialogFragment<BottomSheetQuoteSelectorBinding>(
    BottomSheetQuoteSelectorBinding::inflate
) {
    private val quoteAdapter = QuoteAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        quoteAdapter.setListener(object : QuoteAdapter.Listener {
            override fun onSelectQuote(quote: QuoteUIModel) {
                onQuoteSelected(quote)
                dismiss()
            }
        })
        binding.rvQuotes.adapter = quoteAdapter
        quoteAdapter.submitList(quotes)

        binding.tabCategories.removeAllTabs()
        categories.forEach { category ->
            binding.tabCategories.addTab(binding.tabCategories.newTab().setText(category))
        }

        binding.tabCategories.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.text?.toString()?.let { category ->
                    onCategorySelected(category)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    fun updateQuotes(newQuotes: List<QuoteUIModel>) {
        quoteAdapter.submitList(newQuotes)
    }
}
