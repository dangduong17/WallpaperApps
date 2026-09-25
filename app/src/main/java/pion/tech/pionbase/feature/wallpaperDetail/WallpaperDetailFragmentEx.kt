package pion.tech.pionbase.feature.wallpaperDetail

import android.app.WallpaperManager
import android.content.Intent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import pion.tech.pionbase.R
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.feature.home.EditWallpaperActivity
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.isGif
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.loadThumbnailAndFull
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import timber.log.Timber

fun WallpaperDetailFragment.initView() {
    // Initialization logic if needed
}

fun WallpaperDetailFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.safeNavigateUp()
    }

    binding.fabFavorite.setPreventDoubleClickScaleView {
        // Clear previous animation if any
        binding.fabFavorite.clearAnimation()
        
        // Create and store animation
        favoriteAnim = ScaleAnimation(
            1.0f, 1.5f, 1.0f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
        ).apply {
            duration = 400
            interpolator = OvershootInterpolator()
        }
        
        binding.fabFavorite.startAnimation(favoriteAnim)
        viewModel.toggleFavorite()
    }

    binding.btnSetWallpaper.setPreventDoubleClickScaleView {
        val context = requireContext()
        val isGif = viewModel.uiState.value.isGif || (wallpaperUri?.isGif(context.contentResolver) == true)

        if (isGif) {
            val uriToUse = wallpaperUri ?: viewModel.uiState.value.wallpaper?.imageUrl?.toUri()
            if (uriToUse != null) {
                val intent = Intent(context, EditWallpaperActivity::class.java).apply {
                    putExtra(pion.tech.pionbase.util.Constant.KEY_URI, uriToUse)
                }
                startActivity(intent)
            } else {
                displayToast(getString(R.string.error_set_gif))
            }
            return@setPreventDoubleClickScaleView
        }

        val options = arrayOf(
            context.getString(R.string.home_screen),
            context.getString(R.string.lock_screen),
            context.getString(R.string.both)
        )
        MaterialAlertDialogBuilder(context)
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
                    val imageUrl = viewModel.uiState.value.wallpaper?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        viewModel.applyWallpaperFromUrl(imageUrl, flag)
                    } else {
                        displayToast(getString(R.string.please_wait))
                    }
                }
            }
            .show()
    }
    
    binding.fabAddQuote.setPreventDoubleClickScaleView {
        val currentWallpaper = viewModel.uiState.value.wallpaper
        if (currentWallpaper != null) {
            val action = WallpaperDetailFragmentDirections.actionWallpaperDetailFragmentToQuoteEditorFragment(currentWallpaper)
            navigator.safeNavigate(action)
        }
    }

    binding.fabDownload.setPreventDoubleClickScaleView {
        checkPermissionAndDownload()
    }

    binding.fabShare.setPreventDoubleClickScaleView {
        viewModel.uiState.value.wallpaper?.let { currentWallpaper ->
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
            binding.ivBackgroundWallpaper.setImageURI(wallpaperUri)
        } else {
            binding.ivBackgroundWallpaper.loadImage(wallpaper.resolvedThumbnailUrl)
            binding.ivFullWallpaper.loadThumbnailAndFull(
                thumbnailUrl = wallpaper.resolvedThumbnailUrl,
                fullUrl = wallpaper.imageUrl
            )
        }
    }
}
