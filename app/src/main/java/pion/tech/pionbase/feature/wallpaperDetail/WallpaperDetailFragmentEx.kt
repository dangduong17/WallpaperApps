package pion.tech.pionbase.feature.wallpaperDetail

import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
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
        // Clear previous animation if any
        binding.fabFavorite.clearAnimation()
        
        // Create and store animation
        favoriteAnim = ScaleAnimation(
            1.0f, 1.5f, 1.0f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 400
            interpolator = OvershootInterpolator()
        }
        
        binding.fabFavorite.startAnimation(favoriteAnim)
        viewModel.toggleFavorite()
    }

    binding.btnSetWallpaper.setPreventDoubleClickScaleView {
        displayToast("Setting wallpaper...")
    }
    
    binding.fabDownload.setPreventDoubleClickScaleView {
        checkPermissionAndDownload()
    }
}

fun WallpaperDetailFragment.releaseAnimation() {
    binding.fabFavorite.clearAnimation()
    favoriteAnim?.setAnimationListener(null)
    favoriteAnim?.cancel()
    favoriteAnim = null
}
