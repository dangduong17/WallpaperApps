package pion.tech.pionbase.feature.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.databinding.ItemSearchSuggestionBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class SuggestionAdapter :
    BaseListAdapter<String, ItemSearchSuggestionBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    
    interface Listener {
        fun onClickSuggestion(suggestion: String)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSearchSuggestionBinding {
        return ItemSearchSuggestionBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemSearchSuggestionBinding,
        item: String,
        position: Int,
    ) {
        binding.tvSuggestion.text = item
        binding.root.setPreventDoubleClick {
            listener?.onClickSuggestion(item)
        }
    }
}
