package pion.tech.pionbase.feature.favorite

import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun FavoriteFragment.initView() {
    adapter.setListener(this)
    if (binding.rvFavorites.adapter == null) {
        binding.rvFavorites.adapter = adapter
    }
}

fun FavoriteFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.safeNavigateUp()
    }
}
