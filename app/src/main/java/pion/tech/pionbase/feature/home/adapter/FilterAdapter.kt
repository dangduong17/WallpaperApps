package pion.tech.pionbase.feature.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.databinding.ItemFilterBinding
import pion.tech.pionbase.util.setPreventDoubleClick

data class FilterUIModel(
    val name: String,
    val isSelected: Boolean = false
)

class FilterAdapter :
    BaseListAdapter<FilterUIModel, ItemFilterBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.name == newItem.name },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    interface Listener {
        fun onClickFilter(item: FilterUIModel)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemFilterBinding {
        return ItemFilterBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemFilterBinding,
        item: FilterUIModel,
        position: Int,
    ) {
        binding.apply {
            tvFilterName.text = item.name
            if (item.isSelected) {
                cardFilter.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.color_pink_E91E63))
                cardFilter.strokeWidth = 0
                tvFilterName.setTextColor(ContextCompat.getColor(root.context, R.color.white))
            } else {
                cardFilter.setCardBackgroundColor(Color.TRANSPARENT)
                cardFilter.strokeWidth = 1
                cardFilter.strokeColor = ContextCompat.getColor(root.context, R.color.divider_color)
                tvFilterName.setTextColor(ContextCompat.getColor(root.context, R.color.text_primary))
            }
            
            root.setPreventDoubleClick {
                listener?.onClickFilter(item)
            }
        }
    }
}
