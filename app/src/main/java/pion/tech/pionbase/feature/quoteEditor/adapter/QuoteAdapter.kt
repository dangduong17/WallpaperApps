package pion.tech.pionbase.feature.quoteEditor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.quote.QuoteUIModel
import pion.tech.pionbase.databinding.ItemQuoteBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class QuoteAdapter : BaseListAdapter<QuoteUIModel, ItemQuoteBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {
    interface Listener {
        fun onSelectQuote(quote: QuoteUIModel)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemQuoteBinding {
        return ItemQuoteBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemQuoteBinding, item: QuoteUIModel, position: Int) {
        binding.apply {
            tvQuoteText.text = "\"${item.quote}\""
            tvQuoteAuthor.text = if (item.author.isNotEmpty()) "- ${item.author}" else ""

            root.setPreventDoubleClick {
                listener?.onSelectQuote(item)
            }
        }
    }
}
