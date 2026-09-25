package pion.tech.pionbase.feature.urlWallpaper

import com.bumptech.glide.Glide
import pion.tech.pionbase.R
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun UrlWallpaperFragment.initView() {
    // No specific initialization needed for now
}

fun UrlWallpaperFragment.settingEvent() {
    binding.btnBack.setPreventDoubleClickScaleView {
        navigator.safeNavigateUp()
    }

    binding.btnPreview.setPreventDoubleClickScaleView {
        handlePreviewWallpaper()
    }

    binding.btnSetWallpaper.setPreventDoubleClickScaleView {
        handleSetWallpaper()
    }
}

fun UrlWallpaperFragment.handlePreviewWallpaper() {
    val url = binding.etImageUrl.text.toString().trim()
    if (url.isNotEmpty()) {
        Glide.with(this)
            .load(url)
            .into(binding.ivPreview)
    } else {
        displayToast(getString(R.string.please_enter_url))
    }
}

fun UrlWallpaperFragment.handleSetWallpaper() {
    val url = binding.etImageUrl.text.toString().trim()
    if (url.isNotEmpty()) {
        viewModel.setWallpaperFromUrl(url)
    } else {
        displayToast(getString(R.string.please_enter_url))
    }
}
