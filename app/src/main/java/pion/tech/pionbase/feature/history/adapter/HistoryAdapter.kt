package pion.tech.pionbase.feature.history.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.history.WallpaperHistoryUIModel
import pion.tech.pionbase.databinding.ItemHistoryWallpaperBinding
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick

class HistoryAdapter :
    BaseListAdapter<WallpaperHistoryUIModel, ItemHistoryWallpaperBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {

    interface Listener {
        fun onClickHistoryItem(item: WallpaperHistoryUIModel)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemHistoryWallpaperBinding {
        return ItemHistoryWallpaperBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemHistoryWallpaperBinding,
        item: WallpaperHistoryUIModel,
        position: Int,
    ) {
        binding.apply {
            ivWallpaper.loadImage(item.resolvedThumbnailUrl)
            tvTitle.text = item.title.ifEmpty { item.categoryName }

            val timeAgo = DateUtils.getRelativeTimeSpanString(
                item.timestamp,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
            tvTime.text = timeAgo

            root.setPreventDoubleClick {
                listener?.onClickHistoryItem(item)
            }
        }
    }
}
