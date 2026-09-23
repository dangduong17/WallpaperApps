package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.ItemWallpaperBinding
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick

class TopWallpaperAdapter :
    BaseListAdapter<WallpaperUIModel, ItemWallpaperBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.title == newItem.title },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    interface Listener {
        fun onClickWallpaper(item: WallpaperUIModel)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemWallpaperBinding {
        return ItemWallpaperBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemWallpaperBinding,
        item: WallpaperUIModel,
        position: Int,
    ) {
        binding.apply {
            ivWallpaper.loadImage(item.resolvedThumbnailUrl)
            root.setPreventDoubleClick {
                listener?.onClickWallpaper(item)
            }
        }
    }
}
