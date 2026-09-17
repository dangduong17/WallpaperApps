package pion.tech.pionbase.feature.categoryDetail

import androidx.navigation.fragment.navArgs
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun CategoryDetailFragment.initView() {
    val args: CategoryDetailFragmentArgs by navArgs()
    binding.tvTitle.text = args.categoryName
    
    adapter.setListener(this)
    binding.rvWallpapers.adapter = adapter
}

fun CategoryDetailFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.navigateUp()
    }
}
