package pion.tech.pionbase.feature.urlWallpaper

import com.bumptech.glide.Glide
import pion.tech.pionbase.R
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun UrlWallpaperFragment.initView() {
    // No specific initialization needed for now
}

fun UrlWallpaperFragment.settingEvent() {
    binding.apply {
        btnBack.setPreventDoubleClickScaleView {
            navigator.navigateUp()
        }

        btnPreview.setPreventDoubleClickScaleView {
            val url = etImageUrl.text.toString()
            if (url.isNotEmpty()) {
                Glide.with(this@settingEvent)
                    .load(url)
                    .into(ivPreview)
            } else {
                displayToast(getString(R.string.please_enter_url))
            }
        }

        btnSetWallpaper.setPreventDoubleClickScaleView {
            val url = etImageUrl.text.toString()
            if (url.isNotEmpty()) {
                viewModel.setWallpaperFromUrl(url)
            } else {
                displayToast(getString(R.string.please_enter_url))
            }
        }
    }
}
