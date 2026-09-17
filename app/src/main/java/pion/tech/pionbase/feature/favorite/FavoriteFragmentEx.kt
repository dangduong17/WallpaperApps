package pion.tech.pionbase.feature.favorite

import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun FavoriteFragment.initView() {
    adapter.setListener(this)
    binding.rvFavorites.adapter = adapter
}

fun FavoriteFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.navigateUp()
    }
}
