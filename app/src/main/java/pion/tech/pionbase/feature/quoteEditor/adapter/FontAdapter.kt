package pion.tech.pionbase.feature.quoteEditor.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.databinding.ItemFontBinding
import pion.tech.pionbase.util.setPreventDoubleClick

data class FontItem(
    val name: String,
    val fontResId: Int? = null,
    val typeface: Typeface? = null
)

class FontAdapter : BaseListAdapter<FontItem, ItemFontBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem.name == newItem.name },
        areContentsTheSame = { oldItem, newItem -> oldItem.name == newItem.name }
    )
) {
    interface Listener {
        fun onSelectFont(fontItem: FontItem)
    }

    private var listener: Listener? = null
    private var selectedPosition = 0

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemFontBinding {
        return ItemFontBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemFontBinding, item: FontItem, position: Int) {
        val typefaceToApply = when {
            item.fontResId != null -> ResourcesCompat.getFont(binding.root.context, item.fontResId)
            item.typeface != null -> item.typeface
            else -> Typeface.DEFAULT
        }

        binding.apply {
            tvFontSample.text = item.name
            tvFontSample.typeface = typefaceToApply

            val context = root.context
            val strokeColor = if (position == selectedPosition) {
                context.getColor(R.color.white)
            } else {
                android.graphics.Color.TRANSPARENT
            }
            root.strokeColor = strokeColor

            root.setPreventDoubleClick {
                val oldPos = selectedPosition
                selectedPosition = position
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
                listener?.onSelectFont(item)
            }
        }
    }
}
