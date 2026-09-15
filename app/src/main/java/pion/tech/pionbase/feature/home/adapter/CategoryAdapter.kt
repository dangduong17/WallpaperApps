package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.wallpaper.CategoryUIModel
import pion.tech.pionbase.databinding.ItemCategoryBinding

class CategoryAdapter :
    BaseListAdapter<CategoryUIModel, ItemCategoryBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.title == newItem.title },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCategoryBinding {
        return ItemCategoryBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemCategoryBinding,
        item: CategoryUIModel,
        position: Int,
    ) {
        binding.apply {
            tvCategoryTitle.text = item.title
            Glide.with(ivCategory).load(item.imageUrl).into(ivCategory)
        }
    }
}
