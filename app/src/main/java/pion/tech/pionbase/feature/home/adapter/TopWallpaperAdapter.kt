package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.ItemWallpaperBinding

class TopWallpaperAdapter :
    BaseListAdapter<WallpaperUIModel, ItemWallpaperBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.title == newItem.title },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {

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
            Glide.with(ivWallpaper).load(item.imageUrl).into(ivWallpaper)
        }
    }
}
