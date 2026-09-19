package pion.tech.pionbase.feature.wallpaperDetail

import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
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
        val options = arrayOf("Màn hình chính", "Màn hình khóa", "Cả hai")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Đặt làm hình nền")
            .setItems(options) { _, which ->
                val flag = when(which) {
                    0 -> android.app.WallpaperManager.FLAG_SYSTEM
                    1 -> android.app.WallpaperManager.FLAG_LOCK
                    else -> android.app.WallpaperManager.FLAG_SYSTEM or android.app.WallpaperManager.FLAG_LOCK
                }

                wallpaperUri?.let { uri ->
                    viewModel.applyWallpaper(uri, flag)
                } ?: run {
                    val bitmap = (binding.ivFullWallpaper.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                    if (bitmap != null) {
                        showHideLoading(true)
                        launchIO {
                            try {
                                val wallpaperManager = android.app.WallpaperManager.getInstance(requireContext())
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    wallpaperManager.setBitmap(bitmap, null, true, flag)
                                } else {
                                    wallpaperManager.setBitmap(bitmap)
                                }
                                launchMain {
                                    showHideLoading(false)
                                    displayToast("Đã đặt hình nền thành công!")
                                }
                            } catch (e: Exception) {
                                launchMain {
                                    showHideLoading(false)
                                    displayToast("Không thể đặt hình nền: ${e.message}")
                                }
                            }
                        }
                    } else {
                        displayToast("Đang tải ảnh, vui lòng đợi...")
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
