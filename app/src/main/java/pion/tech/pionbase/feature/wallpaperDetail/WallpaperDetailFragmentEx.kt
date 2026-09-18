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
        val wallpaperUrl = viewModel.uiState.value.wallpaper?.imageUrl ?: return@setPreventDoubleClickScaleView
        
        showHideLoading(true)
        
        launchIO {
            val file = try {
                val connection = java.net.URL(wallpaperUrl).openConnection() as java.net.HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val targetFile = java.io.File(requireContext().cacheDir, "current_wallpaper.mp4")
                val outputStream = java.io.FileOutputStream(targetFile)
                inputStream.copyTo(outputStream)
                targetFile
            } catch (e: Exception) {
                null
            }
            
            launchMain {
                showHideLoading(false)
                if (file != null && file.exists()) {
                    val sharedPref = requireContext().getSharedPreferences("wallpaper_prefs", android.content.Context.MODE_PRIVATE)
                    sharedPref.edit().putString("wallpaper_path", file.absolutePath).apply()
                    
                    val intent = android.content.Intent(android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                    intent.putExtra(
                        android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        android.content.ComponentName(requireContext(), pion.tech.pionbase.service.VideoWallpaperService::class.java)
                    )
                    startActivity(intent)
                } else {
                    displayToast("Không thể tải hình nền")
                }
            }
        }
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
