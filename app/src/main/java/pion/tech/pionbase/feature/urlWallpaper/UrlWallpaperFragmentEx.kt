package pion.tech.pionbase.feature.urlWallpaper

import com.bumptech.glide.Glide
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun UrlWallpaperFragment.initView() {
    // No specific initialization needed for now
}

fun UrlWallpaperFragment.settingEvent() {
    binding.apply {
        btnBack.setOnClickListener {
            navigator.navigateUp()
        }

        btnPreview.setPreventDoubleClickScaleView {
            val url = etImageUrl.text.toString()
            if (url.isNotEmpty()) {
                Glide.with(this@settingEvent)
                    .load(url)
                    .into(ivPreview)
            } else {
                displayToast("Please enter a URL")
            }
        }

        btnSetWallpaper.setPreventDoubleClickScaleView {
            val url = etImageUrl.text.toString()
            if (url.isNotEmpty()) {
                viewModel.setWallpaperFromUrl(url)
            } else {
                displayToast("Please enter a URL")
            }
        }
    }
}
