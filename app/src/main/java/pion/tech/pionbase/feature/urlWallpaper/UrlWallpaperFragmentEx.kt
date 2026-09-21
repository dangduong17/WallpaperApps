package pion.tech.pionbase.feature.urlWallpaper

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
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
