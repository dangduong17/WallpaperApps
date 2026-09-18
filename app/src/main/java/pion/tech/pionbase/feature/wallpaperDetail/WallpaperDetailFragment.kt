package pion.tech.pionbase.feature.wallpaperDetail

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import android.view.View
import android.view.animation.Animation
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentWallpaperDetailBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class WallpaperDetailFragment : BaseFragment<FragmentWallpaperDetailBinding, WallpaperDetailViewModel>(
    FragmentWallpaperDetailBinding::inflate,
    WallpaperDetailViewModel::class
) {
    private val args: WallpaperDetailFragmentArgs by navArgs()
    var favoriteAnim: Animation? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        timber.log.Timber.d("PermissionCheck Result: $isGranted")
        if (isGranted) {
            viewModel.downloadWallpaper()
        } else {
            displayToast("Cần cấp quyền để lưu ảnh")
        }
    }

    override fun init(view: View, savedInstanceState: Bundle?) {
        viewModel.setWallpaper(args.wallpaper)
        initView()
        settingEvent()
    }

    fun checkPermissionAndDownload() {
        timber.log.Timber.d("Check Permission Started")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            viewModel.downloadWallpaper()
        }
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.wallpaper }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { wallpaper ->
                wallpaper?.let {
                    binding.ivFullWallpaper.loadImage(it.imageUrl)
                }
            }

        viewModel.uiState
            .map { it.isFavorite }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isFavorite ->
                val icon = if (isFavorite) {
                    R.drawable.ic_heart_filled
                } else {
                    R.drawable.ic_heart
                }
                binding.fabFavorite.setImageResource(icon)
                
                // Clear color filter if using ic_heart_filled which is already red
                if (isFavorite) {
                    binding.fabFavorite.clearColorFilter()
                } else {
                    binding.fabFavorite.setColorFilter(android.graphics.Color.BLACK)
                }
            }

        viewModel.uiState
            .map { it.isLoading }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    binding.fabDownload.setImageResource(android.R.drawable.stat_notify_sync)
                    binding.fabDownload.isEnabled = false
                } else {
                    binding.fabDownload.setImageResource(android.R.drawable.stat_sys_download)
                    binding.fabDownload.isEnabled = true
                }
            }
    }

    override fun onDestroyView() {
        releaseAnimation()
        super.onDestroyView()
    }
}
