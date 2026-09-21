package pion.tech.pionbase.feature.wallpaperDetail

import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import pion.tech.pionbase.R
import android.app.AlertDialog
import android.app.WallpaperManager
import android.os.Build
import com.google.android.material.snackbar.Snackbar
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
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
        val context = requireContext()
        val options = arrayOf(
            context.getString(R.string.home_screen),
            context.getString(R.string.lock_screen),
            context.getString(R.string.both)
        )
        AlertDialog.Builder(context)
            .setTitle(R.string.set_as_wallpaper)
            .setItems(options) { _, which ->
                val flag = when(which) {
                    0 -> WallpaperManager.FLAG_SYSTEM
                    1 -> WallpaperManager.FLAG_LOCK
                    else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }

                wallpaperUri?.let { uri ->
                    viewModel.applyWallpaper(uri, flag)
                } ?: run {
                    val bitmap = (binding.ivFullWallpaper.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                    if (bitmap != null) {
                        showHideLoading(true)
                        launchIO {
                            try {
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                    val wallpaperManager = WallpaperManager.getInstance(context)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        wallpaperManager.setBitmap(bitmap, null, true, flag)
                                    } else {
                                        wallpaperManager.setBitmap(bitmap)
                                    }
                                }
                                launchMain {
                                    showHideLoading(false)
                                    Snackbar.make(binding.root, R.string.set_wallpaper_success, Snackbar.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                launchMain {
                                    showHideLoading(false)
                                    Snackbar.make(binding.root, context.getString(R.string.error, e.message ?: ""), Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    } else {
                        Snackbar.make(binding.root, R.string.please_wait, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
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
