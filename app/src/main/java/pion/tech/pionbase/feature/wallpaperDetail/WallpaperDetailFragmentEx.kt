package pion.tech.pionbase.feature.wallpaperDetail

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.os.Build
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import pion.tech.pionbase.R
import pion.tech.pionbase.base.doActionWhenResume
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.feature.home.EditWallpaperActivity
import pion.tech.pionbase.util.isGif
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import timber.log.Timber

fun WallpaperDetailFragment.initView() {
    // Initialization logic if needed
}

fun WallpaperDetailFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        doActionWhenResume {
            navigator.navigateUp()
        }
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
        val isGif = viewModel.uiState.value.isGif || wallpaperUri?.isGif(context.contentResolver) == true

        if (isGif) {
            val uriToUse = wallpaperUri ?: viewModel.uiState.value.wallpaper?.imageUrl?.toUri()
            if (uriToUse != null) {
                val intent = Intent(context, EditWallpaperActivity::class.java).apply {
                    putExtra("uri", uriToUse)
                }
                startActivity(intent)
            } else {
                Snackbar.make(binding.root, R.string.error_set_gif, Snackbar.LENGTH_SHORT).show()
            }
            return@setPreventDoubleClickScaleView
        }

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

    binding.fabShare.setPreventDoubleClickScaleView {
        val currentWallpaper = viewModel.uiState.value.wallpaper
        if (currentWallpaper != null) {
            shareWallpaper(currentWallpaper)
        }
    }
}

fun WallpaperDetailFragment.shareWallpaper(wallpaper: WallpaperUIModel) {
    val imageUrl = wallpaper.imageUrl

    val shareIntent = if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, wallpaper.title)
            putExtra(Intent.EXTRA_TEXT, "${wallpaper.title}\n$imageUrl")
        }
    } else {
        val uriToShare = wallpaperUri ?: imageUrl.toUri()
        Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uriToShare)
            putExtra(Intent.EXTRA_TEXT, wallpaper.title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    val chooser = Intent.createChooser(shareIntent, getString(R.string.share_with_friend))
    startActivity(chooser)
}

fun WallpaperDetailFragment.releaseAnimation() {
    binding.fabFavorite.clearAnimation()
    favoriteAnim?.setAnimationListener(null)
    favoriteAnim?.cancel()
    favoriteAnim = null
}

fun WallpaperDetailFragment.handleWallpaperLoading(wallpaper: WallpaperUIModel, isGif: Boolean) {
    Timber.d("DEBUG: URL=${wallpaper.imageUrl}, isGif=$isGif")
    if (isGif) {
        Glide.with(requireContext())
            .asGif()
            .load(wallpaper.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(binding.ivFullWallpaper)
    } else {
        // Nếu là ảnh từ Picker, dùng setImageURI
        if (wallpaperUri != null) {
            binding.ivFullWallpaper.setImageURI(wallpaperUri)
        } else {
            binding.ivFullWallpaper.loadImage(wallpaper.imageUrl)
        }
    }
}
