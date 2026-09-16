package pion.tech.pionbase.feature.wallpaperDetail

import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun WallpaperDetailFragment.initView() {
    // Initialization logic if needed
}

fun WallpaperDetailFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.navigateUp()
    }

    binding.fabFavorite.setPreventDoubleClickScaleView {
        displayToast("Added to favorites")
    }

    binding.btnSetWallpaper.setPreventDoubleClickScaleView {
        displayToast("Setting wallpaper...")
    }
}
