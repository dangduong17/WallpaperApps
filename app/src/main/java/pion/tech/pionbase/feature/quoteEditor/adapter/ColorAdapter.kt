package pion.tech.pionbase.feature.quoteEditor.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.databinding.ItemColorBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class ColorAdapter : BaseListAdapter<Int, ItemColorBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
        areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {
    interface Listener {
        fun onSelectColor(color: Int)
    }

    private var listener: Listener? = null
    private var selectedPosition = 0

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemColorBinding {
        return ItemColorBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemColorBinding, item: Int, position: Int) {
        binding.apply {
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(item)
                if (item == Color.BLACK || item == Color.TRANSPARENT) {
                    setStroke(2, Color.WHITE)
                } else if (item == Color.WHITE) {
                    setStroke(2, Color.GRAY)
                }
            }
            vColor.background = drawable

            ivSelected.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

            root.setPreventDoubleClick {
                val oldPos = selectedPosition
                selectedPosition = position
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
                listener?.onSelectColor(item)
            }
        }
    }
}
