package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.ItemFeaturedBinding
import pion.tech.pionbase.util.loadImage

class FeaturedAdapter :
    BaseListAdapter<WallpaperUIModel, ItemFeaturedBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.title == newItem.title },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemFeaturedBinding {
        return ItemFeaturedBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemFeaturedBinding,
        item: WallpaperUIModel,
        position: Int,
    ) {
        binding.apply {
            tvFeaturedTitle.text = item.title
            ivFeatured.loadImage(item.imageUrl)
        }
    }
}
