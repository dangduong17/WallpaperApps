package pion.tech.pionbase.feature.categoryDetail

import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun CategoryDetailFragment.initView() {
    binding.tvTitle.text = args.categoryName
    
    adapter.setListener(this)
    if (binding.rvWallpapers.adapter == null) {
        binding.rvWallpapers.adapter = adapter
    }
}

fun CategoryDetailFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.safeNavigateUp()
    }
}
